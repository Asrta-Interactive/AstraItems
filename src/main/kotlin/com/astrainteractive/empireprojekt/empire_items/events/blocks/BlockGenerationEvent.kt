package com.astrainteractive.astraitems.events.block

import com.astrainteractive.astralibs.*
import com.astrainteractive.empireprojekt.EmpirePlugin
import com.astrainteractive.empireprojekt.empire_items.api.items.BlockParser
import com.astrainteractive.empireprojekt.empire_items.api.items.data.ItemManager
import com.astrainteractive.empireprojekt.empire_items.util.Config
import org.apache.commons.codec.digest.DigestUtils
import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.configuration.file.FileConfiguration

import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.event.world.ChunkUnloadEvent
import kotlin.concurrent.timer
import kotlin.random.Random

class BlockGenerationEvent : IAstraListener {

    companion object {
        private val TPS_TRESHHOLD = 19.9
        private val CHUNK_LOAD_GAP = 100L
        private var currentChunkLoadGap = System.currentTimeMillis()
        private var currentChunkAmount = 0
    }

    fun increaseCurrentChunk() = synchronized(this) {
        currentChunkAmount++
    }

    fun decreaseCurrentChunk() = synchronized(this) {
        currentChunkAmount--
    }

//    private var blockQueue = mutableListOf<QueuedBlock>()

    /**
     * Получаем список координат блоков, которые необходимо будет заменить
     */
    private fun Chunk.getBlocksLocations(yMin: Int, yMax: Int, types: List<String>): Map<String, Set<Location>> {
        val locations = types.associateWith { mutableSetOf<Location>() }

        (yMin until yMax).forEach { y ->
            for (x in 0 until 15) {
                (0 until 15).forEach { z ->
                    val loc = Location(world, this.x * 16.0 + x, y * 1.0, this.z * 16.0 + z)
                    if (types.contains(loc.block.type.name))
                        locations[loc.block.type.name]?.add(loc)
                }
            }
        }
        return locations
    }

    private fun getRandomBlockFace(): BlockFace {
        val faces = BlockFace.values()
        return faces[Random.nextInt(faces.size)]
    }

    data class QueuedBlock(
        val l: Location,
        val m: Material,
        val f: Map<String, Boolean>
    ) {
        companion object {
            private var queueFile: FileManager? = FileManager("temp/queue.yml")
            private var config: FileConfiguration? = queueFile?.getConfig()
            fun load() {
                queueFile = FileManager("temp/queue.yml")
                config = queueFile?.getConfig()
            }

            fun save() {
                queueFile?.saveConfig()
            }

            fun close() {
                queueFile = null
                config = null
            }

            fun add(list: List<QueuedBlock>) {
                list.forEach { add(it) }
                save()
            }

            fun add(q: QueuedBlock) {
                val c = config
                val key = DigestUtils.sha1Hex(q.l.toString())
                c?.set("${key}.m", q.m)
                c?.set("${key}.f", q.f)
                c?.set("${key}.l", q.l)
            }


            fun getLast(): QueuedBlock? {
                val c = config
                val key = (c?.getKeys(false)?.firstOrNull()) ?: return null
                catchingNoStackTrace {
                    val l = (c?.getLocation("${key}.l")) ?: return null
                    val m = (c?.getObject("${key}.m", Material::class.java)) ?: return null
                    val f = (c?.getObject("${key}.f", Map::class.java) as Map<String, Boolean>?) ?: return null
                    c.set("$key", null)
                    save()
                    return QueuedBlock(l, m, f)
                }
                c.set("$key", null)
                save()
                return null
            }

            fun size(): Int = synchronized(this) {
                catchingNoStackTrace {
                    config?.getKeys(false)?.size
                } ?: -1
            }
        }

    }

    init {
        QueuedBlock.load()
    }

    private fun addBlockToQueue(_blockQueue: List<QueuedBlock>) =
        synchronized(this) {
            if (Config.generationDeepDebug)
                Logger.log(TAG, "Adding blocks to Queue ${_blockQueue}")
            QueuedBlock.add(_blockQueue)
        }


    private fun getQueuedBlock() = synchronized(this) {
        return@synchronized QueuedBlock.getLast()
    }


    private fun generateBlock() {
        val block = getQueuedBlock() ?: return
        if (Config.generationDebug)
            Logger.log(
                TAG,
                "Generating block at [${block.l.x};${block.l.y};${block.l.z}] queue=${QueuedBlock.size()}"
            )
        callSyncMethod {
            val time = System.currentTimeMillis()
            replaceBlock(block)
            if (Config.generationDebug)
                Logger.log(TAG, "Block replacing time = ${(System.currentTimeMillis() - time)}")
        }
    }

    //Заменяем блок на сгенерированный
    private fun replaceBlock(b: QueuedBlock) {
        val chunkBlock = b.l.block
        BlockParser.setTypeFast(chunkBlock, b.m, b.f)
    }

    /**
     * Создание блоков в чанке
     */
    private fun generateChunk(chunk: Chunk) {
        if (Config.generationDeepDebug)
            Logger.log(TAG, "Generating Queue")
        val currentBlocksQueue = mutableListOf<QueuedBlock>()

        for (itemInfo in ItemManager.getBlocksInfos()) {
            val block = itemInfo.block ?: continue
            //Надо ли генерировать блок
            val generate = block.generate ?: continue
            //Если указан мир и он не равен миру чанка - пропускаем
            if (block.generate.world != null && block.generate.world != chunk.world.name)
                continue
            //Проверяем рандом
            if (generate.generateInChunkChance < Random.nextDouble(100.0))
                continue

            //Получаем максимальное количество блоков в месторождении
            var deposits = Random.nextInt(generate.minPerDeposit, generate.maxPerDeposit + 1)
            deposits =
                if (deposits >= generate.maxPerChunk)
                    generate.maxPerChunk
                else deposits
            //Получаем список локаций блоков по их типу
            val blockLocByType =
                chunk.getBlocksLocations(
                    generate.minY ?: 0,
                    generate.maxY ?: 20,
                    generate.replaceBlocks?.keys?.toList() ?: listOf()
                )
            if (blockLocByType.isEmpty())
                continue

            val material = BlockParser.getMaterialByData(block.data)
            val facing = BlockParser.getFacingByData(block.data)

            generate.replaceBlocks?.forEach allblocks@{ (type, chance) ->
                if (deposits <= 0)
                    return@allblocks
                //Берем список локаций по текущему блоку если они существуют
                val replaceBlocks = blockLocByType[type] ?: return@allblocks
                if (replaceBlocks.isEmpty())
                    return@allblocks
                val toGenerate = Random.nextInt(0, deposits + 1)

                (0 until toGenerate).forEach block@{ i ->
                    //Вероятность создания блока
                    if (chance < Random.nextDouble(100.0))
                        return@block
                    //Берем рандомную локацию из списка локация для замены
                    val blockToReplace = replaceBlocks.elementAt(Random.nextInt(replaceBlocks.size))

                    //Берем количество в месторождении для текущей локации
                    val depositAmount = Random.nextInt(generate.minPerDeposit, generate.maxPerDeposit + 1)
                    var faceBlock = blockToReplace.block
                    (0 until depositAmount).forEach { _ ->
                        if (deposits <= 0)
                            return@allblocks
                        faceBlock = faceBlock.getRelative(getRandomBlockFace())
                        currentBlocksQueue.add(QueuedBlock(faceBlock.location.clone(), material, facing))
                        deposits--
                    }
                }

            }
        }
        addBlockToQueue(currentBlocksQueue)


    }

    @EventHandler
    private fun chunkLoadEvent(e: ChunkLoadEvent) {
        if (currentChunkAmount > Config.generateMaxChunksAtOnce)
            return
        currentChunkLoadGap = System.currentTimeMillis()
        val chunk = e.chunk
        //Если чанк есть в конфиге - значит он уже генерировался
        if (EmpirePlugin.empireFiles.tempChunks.getConfig().contains(chunk.toString()))
            return
        if (!e.isNewChunk && Config.generateOnlyOnNewChunks)
            return
        if (!Config.generateBlocks)
            return
        synchronized(this) {
            val tempChunks = EmpirePlugin.empireFiles.tempChunks
            tempChunks.getConfig().set(chunk.toString(), true)
            tempChunks.saveConfig()
        }
        runAsyncTask {
            increaseCurrentChunk()
            generateChunk(chunk)
            decreaseCurrentChunk()
        }
    }

    val task = Bukkit.getScheduler().runTaskTimer(AstraLibs.instance, Runnable {
        runAsyncTask {
                generateBlock()
        }
    }, 0L, Config.generateBlocksGap)


    val TAG = this.javaClass.name
    override fun onEnable(manager: IAstraManager): IAstraListener {
        if (Config.generationDebug) {
            Logger.log(TAG, "Генерация включена. Таймер генераций включен: ${!task.isCancelled}")
        }
        return super.onEnable(manager)
    }

    override fun onDisable() {
        ChunkLoadEvent.getHandlerList().unregister(this)
        ChunkUnloadEvent.getHandlerList().unregister(this)
        PlayerJoinEvent.getHandlerList().unregister(this)
        PlayerQuitEvent.getHandlerList().unregister(this)
        PlayerMoveEvent.getHandlerList().unregister(this)
        task.cancel()
        QueuedBlock.save()
        QueuedBlock.close()

    }
}