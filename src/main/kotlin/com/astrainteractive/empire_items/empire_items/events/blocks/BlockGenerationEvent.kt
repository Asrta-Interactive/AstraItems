package com.astrainteractive.empire_items.empire_items.events.blocks


import com.astrainteractive.astralibs.*
import com.astrainteractive.empire_items.EmpirePlugin
import com.astrainteractive.empire_items.empire_items.api.items.BlockParser
import com.astrainteractive.empire_items.empire_items.api.items.data.ItemManager
import com.astrainteractive.empire_items.empire_items.util.AsyncHelper
import com.astrainteractive.empire_items.empire_items.util.Config
import com.astrainteractive.empire_items.modules.hud.thirst.RepeatableTask
import kotlinx.coroutines.Dispatchers
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
import kotlin.random.Random

class BlockGenerationEvent : EventListener {

    private var currentChunkAmount = 0
    private val TAG: String
        get() = "BlockGenerationEvent"
    private var blockQueue = mutableListOf<QueuedBlock>()


    /**
     * Получаем список координат блоков, которые необходимо будет заменить
     */
    private fun Chunk.getBlocksLocations(
        yMin: Int,
        yMax: Int,
        types: Map<String, Int>
    ): List<Pair<String, Location>> {
        val xShuffled = (0 until 15).shuffled()
        val zShuffled = xShuffled.shuffled()
        val yShuffled = (yMin until yMax).shuffled()
        val xAndZ = xShuffled zip zShuffled
        val singleMap = yShuffled.flatMap { y ->
            return@flatMap xAndZ.map {
                return@map Pair(y, it)
            }
        }
        return singleMap.mapNotNull { (y, it) ->
            val x = it.first
            val z = it.second
            val loc = Location(this.world, this.x * 16.0 + x, y * 1.0, this.z * 16.0 + z)
            val blockName = loc.block.type.name
            val chance = types[blockName] ?: return@mapNotNull null
            if (chance >= Random.nextInt(0, 100))
                return@mapNotNull Pair(blockName, loc)
            return@mapNotNull null
        }
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
    private fun clearChunks() {
        val tempChunks = EmpirePlugin.empireFiles.tempChunks
        var list = synchronized(this) { blockQueue.toList() }
        list = list.filter { q ->
            q.l.chunk.containsPlayers() || tempChunks.getConfig().contains(q.l.chunk.toString())
        }.toMutableList()
        synchronized(this) {
            blockQueue = list
        }
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
    private fun setChunkHasGenerated(chunk: Chunk, id: String) = synchronized(this) {
        val tempChunks = EmpirePlugin.empireFiles.tempChunks
        if (!tempChunks.getConfig().contains("${chunk}.$id")) {
            tempChunks.getConfig().set("${chunk}.$id", true)
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
                "Generating block at [${block.l.x};${block.l.y};${block.l.z}] queue=${blockQueue.size}", TAG
            )
        setChunkHasGenerated(block.l.chunk, block.id)
        val time = System.currentTimeMillis()
        replaceBlock(block)
        if (Config.generationDebug)
            Logger.log("Block replacing time = ${(System.currentTimeMillis() - time)}", TAG)
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
        val currentBlocksQueue = mutableListOf<QueuedBlock>()

        ItemManager.getBlocksInfos().forEach { itemInfo ->
            val block = itemInfo.block ?: return@forEach
            //Сгенерирован ли блок в чанке
            if (isBlockGeneratedInChunk(chunk, itemInfo.id))
                return@forEach
            //Надо ли генерировать блок
            val generate = block.generate ?: return@forEach
            //Если указан мир и он не равен миру чанка - пропускаем
            if (block.generate.world != null && block.generate.world != chunk.world.name)
                return@forEach
            //Проверяем рандом
            if (generate.generateInChunkChance < Random.nextDouble(100.0)) {
                setChunkHasGenerated(chunk, itemInfo.id)
                return@forEach
            }


            //Получаем список локаций блоков по их типу
            val blockLocByType =
                chunk.getBlocksLocations(
                    generate.minY ?: return@forEach,
                    generate.maxY ?: return@forEach,
                    generate.replaceBlocks ?: return@forEach
                )

            if (blockLocByType.isEmpty())
                return@forEach

            val material = BlockParser.getMaterialByData(block.data)
            val facing = BlockParser.getFacingByData(block.data)
            //Количество сгенерированных блоков
            var generated = 0
            blockLocByType.forEach block@{ (_, loc) ->
                if (generated > generate.maxPerChunk)
                    return@block
                var initialBlock = loc.block
                val originalBlockType = initialBlock.type
                val depositAmount = Random.nextInt(generate.minPerDeposit, generate.maxPerDeposit + 1)
                for (unnamed in 0 until depositAmount) {
                    if (generated > generate.maxPerChunk)
                        return@block
                    for (i in 0 until 10) {
                        val newFaceBlock = initialBlock.getRelative(getRandomBlockFace())
                        if (newFaceBlock.type == originalBlockType) {
                            initialBlock = newFaceBlock
                            break
                        }
                    }
                    val q = QueuedBlock(itemInfo.id, initialBlock.location.clone(), material.name, facing)
                    if (Config.generateBlocksGap == 0L) {
                        replaceBlock(q)
                        setChunkHasGenerated(chunk, itemInfo.id)
                    } else
                        currentBlocksQueue.add(q)
                    generated++
                }

            }
        }
        addBlockToQueue(currentBlocksQueue)
    }

    fun isBlockGeneratedInChunk(chunk: Chunk, id: String): Boolean {
        return EmpirePlugin.empireFiles.tempChunks.getConfig().contains("${chunk}.$id")
    }

    @EventHandler
    private fun chunkLoadEvent(e: ChunkLoadEvent) {
        if (currentChunkAmount > Config.generateMaxChunksAtOnce)
            return
        val chunk = e.chunk

        if (!e.isNewChunk && Config.generateOnlyOnNewChunks)
            return
        if (!Config.generateBlocks)
            return

        currentChunkAmount++
        AsyncHelper.runBackground(Dispatchers.IO) {
            generateChunk(chunk)
            currentChunkAmount--
        }
    }

    var lastChunkCheck = System.currentTimeMillis()

    private val repeatableTask = RepeatableTask(Config.generateBlocksGap) {
        if (Config.generationClearCheck > 0)
            if ((System.currentTimeMillis() - lastChunkCheck) > Config.generationClearCheck) {
                clearChunks()
                lastChunkCheck = System.currentTimeMillis()
            }

        AsyncHelper.runBackground(Dispatchers.IO) {
            generateBlock()
        }
    }


    override fun onEnable(manager: EventManager): EventListener {
        if (Config.generateBlocksGap == 0L)
            repeatableTask.cancel()
        return super.onEnable(manager)
    }

    override fun onDisable() {
        ChunkLoadEvent.getHandlerList().unregister(this)
        ChunkUnloadEvent.getHandlerList().unregister(this)
        PlayerJoinEvent.getHandlerList().unregister(this)
        PlayerQuitEvent.getHandlerList().unregister(this)
        PlayerMoveEvent.getHandlerList().unregister(this)
        repeatableTask.cancel()
        repeatableTask.cancel()
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