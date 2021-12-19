package com.astrainteractive.empireprojekt.empire_items.events.blocks


import com.astrainteractive.astralibs.*
import com.astrainteractive.empireprojekt.EmpirePlugin
import com.astrainteractive.empireprojekt.empire_items.api.items.BlockParser
import com.astrainteractive.empireprojekt.empire_items.api.items.data.ItemManager
import com.astrainteractive.empireprojekt.empire_items.util.Config
import net.minecraft.core.BlockPosition
import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.event.world.ChunkUnloadEvent
import java.awt.Point
import java.sql.Time
import kotlin.concurrent.timer
import kotlin.math.min
import kotlin.random.Random

class BlockGenerationEvent : IAstraListener {

    private var currentChunkAmount = 0
    private var currentChunkLoadGap = System.currentTimeMillis()
    private val TAG: String
        get() = "BlockGenerationEvent"
    private var blockQueue = mutableListOf<QueuedBlock>()
    private fun increaseCurrentChunk() = synchronized(this) {
        currentChunkAmount++
    }

    private fun decreaseCurrentChunk() = synchronized(this) {
        currentChunkAmount--
    }


    /**
     * Получаем список координат блоков, которые необходимо будет заменить
     */
    private fun Chunk.getBlocksLocations(
        yMin: Int,
        yMax: Int,
        types: Map<String,Int>
    ): MutableList<Pair<String, Location>> {
        val locations = mutableListOf<Pair<String, Location>>()
        (yMin until yMax).shuffled().forEach { y ->
            (0 until 15).shuffled().forEach { x ->
                (0 until 15).shuffled().forEach { z ->
                    val loc = Location(this.world, this.x * 16.0 + x, y * 1.0, this.z * 16.0 + z)
                    val blockName = loc.block.type.name
                    if (types.contains(blockName) && types[blockName]!! >= Random.nextInt(0,100))
                        locations.add(Pair(blockName, loc))
                }
            }
        }

        return locations
    }

    /**
     * Получаем рандомное направление чтобы получить связный блок
     */
    private fun getRandomBlockFace(): BlockFace {
        val faces = BlockFace.values()
        return faces[Random.nextInt(faces.size)]
    }

    /**
     * Дистанция от чанка до игрока в 2D координатах
     */
    private fun Chunk.distanceToPlayer(p: Player): Double {
        val p1 = Point(x, z)
        val p2 = Point(p.location.chunk.x, p.location.chunk.z)
        return p1.distance(p2)
    }

    /**
     * Содержит ли чанк игрока, или есть ли вблизи viewDistance игроки
     */
    private fun Chunk.containsPlayers(): Boolean {
        val list = Bukkit.getOnlinePlayers().filter { this.distanceToPlayer(it) < Bukkit.getServer().viewDistance }
        return list.isNotEmpty()
    }

    /**
     * Очистка из очереди чанков, которые не сгенерированы и рядом с которыми нет игроков
     */
    private fun clearChunks() = synchronized(this) {
        val tempChunks = EmpirePlugin.empireFiles.tempChunks
        blockQueue = blockQueue.filter { q ->
            q.l.chunk.containsPlayers() || tempChunks.getConfig().contains(q.l.chunk.toString())
        }.toMutableList()
    }

    /**
     * Добавление блоков в очередь
     */
    private fun addBlockToQueue(_blockQueue: List<QueuedBlock>) =
        synchronized(this) {
            blockQueue.addAll(_blockQueue)
        }

    /**
     * Получение первого блока из очереди и удаление его же
     */
    private fun getQueuedBlock() = synchronized(this) {
        return@synchronized blockQueue.removeFirstOrNull()
    }

    /**
     * Загружает в файл информацию о том, что чанк был сгенерирован
     */
    private fun setChunkHasGenerated(chunk: Chunk) {
        val tempChunks = EmpirePlugin.empireFiles.tempChunks
        if (!tempChunks.getConfig().contains(chunk.toString())) {
            tempChunks.getConfig().set(chunk.toString(), true)
            tempChunks.saveConfig()
        }
    }

    /**
     * Генерация блока
     */
    private fun generateBlock() {
        val block = getQueuedBlock() ?: return
        if (Config.generationDebug)
            Logger.log(
                TAG,
                "Generating block at [${block.l.x};${block.l.y};${block.l.z}] queue=${blockQueue.size}"
            )
        setChunkHasGenerated(block.l.chunk)
        callSyncMethod {
            val time = System.currentTimeMillis()
            replaceBlock(block)
            if (Config.generationDebug)
                Logger.log(TAG, "Block replacing time = ${(System.currentTimeMillis() - time)}")
        }
    }

    /**
     * Заменяем блок на сгенерированный
     */
    private fun replaceBlock(b: QueuedBlock) =
        BlockParser.setTypeFast(b.l.block, b.mat, b.f)


    /**
     * Получение списка локация из чанка и добавление их в очередь
     */
    private fun generateChunk(chunk: Chunk) {
        if (Config.generationDeepDebug)
            Logger.log(TAG, "Generating Queue")
        val currentBlocksQueue = mutableListOf<QueuedBlock>()

        ItemManager.getBlocksInfos().forEach { itemInfo ->
            val block = itemInfo.block ?: return@forEach
            //Надо ли генерировать блок
            val generate = block.generate ?: return@forEach
            //Если указан мир и он не равен миру чанка - пропускаем
            if (block.generate.world != null && block.generate.world != chunk.world.name)
                return@forEach
            //Проверяем рандом
            if (generate.generateInChunkChance < Random.nextDouble(100.0))
                return@forEach


            //Получаем список локаций блоков по их типу
            val blockLocByType =
                chunk.getBlocksLocations(
                    generate.minY ?: return@forEach,
                    generate.maxY ?: return@forEach,
                    generate.replaceBlocks?: return@forEach
                )

            if (blockLocByType.isEmpty())
                return@forEach

            val material = BlockParser.getMaterialByData(block.data)
            val facing = BlockParser.getFacingByData(block.data)
            //Количество сгенерированных блоков
            var generated = 0
            blockLocByType.forEach block@{
                if (generated > generate.maxPerChunk)
                    return@block
                val type = it.first
                val l = it.second
                var faceBlock = l.block
                val originalBlockType = faceBlock.type
                val depositAmount = Random.nextInt(generate.minPerDeposit, generate.maxPerDeposit + 1)
                (0 until depositAmount).forEach deposit@{ _ ->
                    if (generated > generate.maxPerChunk)
                        return@block
                    for (i in 0 until 10) {
                        val newFaceBlock = faceBlock.getRelative(getRandomBlockFace())
                        if (newFaceBlock.type == originalBlockType) {
                            faceBlock = newFaceBlock
                            break
                        }
                    }
                    currentBlocksQueue.add(QueuedBlock(faceBlock.location.clone(), material.name, facing))
                    generated++
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

        increaseCurrentChunk()
        runAsyncTask {
            generateChunk(chunk)
            decreaseCurrentChunk()
        }
    }

    var lastChunkCheck = System.currentTimeMillis()

    private val task = Bukkit.getScheduler().runTaskTimer(AstraLibs.instance, Runnable {
        if ((System.currentTimeMillis() - lastChunkCheck) > Config.generationClearCheck) {
            clearChunks()
            lastChunkCheck = System.currentTimeMillis()
        }

        runAsyncTask {
            generateBlock()
        }
    }, 0L, Config.generateBlocksGap)


    override fun onDisable() {
        ChunkLoadEvent.getHandlerList().unregister(this)
        ChunkUnloadEvent.getHandlerList().unregister(this)
        PlayerJoinEvent.getHandlerList().unregister(this)
        PlayerQuitEvent.getHandlerList().unregister(this)
        PlayerMoveEvent.getHandlerList().unregister(this)
        task.cancel()
    }

}

object Timer {
    private val map: MutableMap<String, Long> = mutableMapOf()

    fun start(key: String) {
        if (map.containsKey(key)) {
            println("$key: ${(System.currentTimeMillis() - map.remove(key)!!) / 1000.0}s")
        } else map[key] = System.currentTimeMillis()
    }

}