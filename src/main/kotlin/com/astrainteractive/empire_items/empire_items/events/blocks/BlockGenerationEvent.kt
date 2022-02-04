package com.astrainteractive.empire_items.empire_items.events.blocks


import com.astrainteractive.astralibs.*
import com.astrainteractive.empire_items.EmpirePlugin
import com.astrainteractive.empire_items.empire_items.api.items.BlockParser
import com.astrainteractive.empire_items.empire_items.api.items.data.ItemManager
import com.astrainteractive.empire_items.empire_items.events.blocks.GenerationUtils.containsPlayers
import com.astrainteractive.empire_items.empire_items.util.Config
import com.astrainteractive.empire_items.empire_items.util.TriplePair
import com.astrainteractive.empire_items.modules.hud.thirst.RepeatableTask
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.event.world.ChunkUnloadEvent
import kotlin.coroutines.CoroutineContext
import kotlin.math.max
import kotlin.random.Random

class BlockGenerationEvent : EventListener, CoroutineScope {
    private val job: Job
        get() = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Default

    private val TAG: String
        get() = "BlockGenerationEvent"
    private var blockQueue = mutableListOf<QueuedBlock>()
    private val chunkList = mutableSetOf<Chunk>()
    private var currentChunkProcessing = 0L
    private fun addChunkToQueue(chunk: Chunk) {
        chunkList.add(chunk)
    }



    private fun getChunkFromQueue(): Chunk? {
        val chunk = chunkList.firstOrNull()
        chunkList.remove(chunk)
        return chunk
    }

    private fun clearChunks() {
        chunkList.removeIf { !it.containsPlayers() }
    }

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
        val singleMap = yShuffled.flatMap { y ->
            return@flatMap xShuffled.flatMap f2@{ x ->
                return@f2 zShuffled.map { z ->
                    return@map TriplePair(x, y, z)
                }
            }
        }
        return singleMap.mapNotNull { (x,y,z) ->
            val loc = Location(world, this.x * 16.0 + x, y * 1.0, this.z * 16.0 + z)
            val blockName = loc.block.type.name
            val chance = types[blockName] ?: return@mapNotNull null
            if (chance >= Random.nextInt(0, 100))
                return@mapNotNull Pair(blockName, loc)
            return@mapNotNull null
        }
    }


    /**
     * Очистка из очереди чанков, которые не сгенерированы и рядом с которыми нет игроков
     */
    private fun clearBlockQueue() {
        val tempChunks = EmpirePlugin.empireFiles.tempChunks
        var list = synchronized(this) { blockQueue.toList() }
        list = list.filter { q ->
            q.location.chunk.containsPlayers() || tempChunks.getConfig().contains(q.location.chunk.toString())
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
    private fun setChunkHasGenerated(chunk: Chunk, id: String) = synchronized(this){
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
            log("Generating block at [${block.location.x};${block.location.y};${block.location.z}] queue=${blockQueue.size}")
        setChunkHasGenerated(block.location.chunk, block.id)
        val time = System.currentTimeMillis()
        replaceBlock(block)
        if (Config.generationDebug)
            log("Block replacing time = ${(System.currentTimeMillis() - time)}")
    }

    /**
     * Заменяем блок на сгенерированный
     */
    private fun replaceBlock(b: QueuedBlock) = Bukkit.getScheduler().runTaskLaterAsynchronously(
        EmpirePlugin.instance,
        Runnable {
            if (Config.generationDeepDebug)
                log("Creating ${b.id} at {${b.location.x}; ${b.location.y}; ${b.location.z}}")
            BlockParser.setTypeFast(b.location.block, b.material, b.faces)
        }, 5L
    )

    private val blocksToGenerate = ItemManager.getBlocksInfos().filter { it.block?.generate != null }

    /**
     * Получение списка локация из чанка и добавление их в очередь
     */
    private fun generateChunk(chunk: Chunk) {
        if (Config.generationDeepDebug)
            log("Generating Queue")
        val currentBlocksQueue = mutableListOf<QueuedBlock>()
        blocksToGenerate.forEach { itemInfo ->
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
            blockLocByType.forEach block@{ (_, location) ->
                if (generated > generate.maxPerChunk)
                    return@block
                var faceBlock = location.block
                val originalBlockType = faceBlock.type
                val depositAmount = Random.nextInt(generate.minPerDeposit, generate.maxPerDeposit + 1)
                for (unnamed in 0 until depositAmount) {
                    if (generated > generate.maxPerChunk)
                        return@block
                    for (i in 0 until 10) {
                        val newFaceBlock = faceBlock.getRelative(GenerationUtils.getRandomBlockFace())
                        if (newFaceBlock.type == originalBlockType) {
                            faceBlock = newFaceBlock
                            break
                        }
                    }
                    val q = QueuedBlock(itemInfo.id, faceBlock.location.clone(), material.name, facing)
                    if (Config.generateBlocksGap <= 0L) {
                        replaceBlock(q)
                        setChunkHasGenerated(chunk, itemInfo.id)
                    } else
                        currentBlocksQueue.add(q)
                    generated++
                }

            }
        }
        if (Config.generationDebug)
            log("Created queue of ${currentBlocksQueue.size} blocks")
        if (Config.generateBlocksGap > 0L)
            addBlockToQueue(currentBlocksQueue)
    }

    private fun isBlockGeneratedInChunk(chunk: Chunk, id: String): Boolean = synchronized(this){
        return EmpirePlugin.empireFiles.tempChunks.getConfig().contains("${chunk}.$id")
    }


    @EventHandler
    private fun chunkLoadEvent(e: ChunkLoadEvent) {
        val chunk = e.chunk
        if (!e.isNewChunk && Config.generateOnlyOnNewChunks)
            return
        if (!Config.generateBlocks)
            return
        if (Config.generateBlocksTimeGap > 0L) addChunkToQueue(chunk)
        else {
            if (currentChunkProcessing >= Config.generateMaxChunksAtOnce)
                return
            currentChunkProcessing++
            launch {
                generateChunk(chunk)
                currentChunkProcessing--
            }
        }
    }


    private val blockGenerationTask = RepeatableTask(max(1, Config.generateBlocksGap)) {
        if (Config.generateBlocksGap <= 0L) return@RepeatableTask
        launch {
            generateBlock()
        }
    }
    private val clearBlocksTask = RepeatableTask(max(1, Config.generationClearCheck)) {
        if (Config.generationClearCheck <= 0L) return@RepeatableTask
        launch {
            clearBlockQueue()
        }
    }

    private val generateChunkTask = RepeatableTask(max(1, Config.generateBlocksTimeGap)) {
        launch {
            if (Config.generateBlocksTimeGap <= 0L) return@launch
            if (Config.generationDebug)
                log("Chunk queue: ${chunkList.size}")
            val chunk = getChunkFromQueue() ?: return@launch
            generateChunk(chunk)
        }
    }
    private val clearOldChunksTask = RepeatableTask(2000L) {
        if (Config.generateBlocksTimeGap <= 0L)
            return@RepeatableTask
        clearChunks()
    }


    override fun onEnable(manager: EventManager): EventListener {
        if (Config.generateBlocksGap <= 0L || !Config.generateBlocks)
            blockGenerationTask.cancel()
        if (Config.generationClearCheck <= 0L || !Config.generateBlocks)
            blockGenerationTask.cancel()
        if (Config.generateBlocksTimeGap <= 0L || !Config.generateBlocks) {
            generateChunkTask.cancel()
            clearOldChunksTask.cancel()
        }
        return super.onEnable(manager)
    }

    fun log(message: String) = Logger.log(message, TAG)
    override fun onDisable() {
        ChunkLoadEvent.getHandlerList().unregister(this)
        ChunkUnloadEvent.getHandlerList().unregister(this)
        PlayerJoinEvent.getHandlerList().unregister(this)
        PlayerQuitEvent.getHandlerList().unregister(this)
        PlayerMoveEvent.getHandlerList().unregister(this)
        blockGenerationTask.cancel()
        clearBlocksTask.cancel()
        generateChunkTask.cancel()
        clearOldChunksTask.cancel()
    }

}

