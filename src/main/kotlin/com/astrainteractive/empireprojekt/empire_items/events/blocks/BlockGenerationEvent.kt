package com.astrainteractive.astraitems.events.block

import com.astrainteractive.astralibs.*
import com.astrainteractive.empireprojekt.EmpirePlugin
import com.astrainteractive.empireprojekt.empire_items.api.items.BlockParser
import com.astrainteractive.empireprojekt.empire_items.api.items.data.ItemManager
import net.minecraft.world.level.GeneratorAccess
import net.minecraft.world.level.block.state.IBlockData
import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.MultipleFacing
import org.bukkit.craftbukkit.v1_17_R1.block.CraftBlock
import org.bukkit.craftbukkit.v1_17_R1.block.data.CraftBlockData
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.event.world.ChunkUnloadEvent
import kotlin.random.Random

class BlockGenerationEvent : IAstraListener {

    companion object {
        private val MAX_QUEUE_SIZE = 300
        private val GENERATE_BLOCK_GAP_TICK = 2L
        private val TPS_TRESHHOLD = 19.9
        private val CHUNK_LOAD_GAP = 100L
        private var currentChunkLoadGap = System.currentTimeMillis()
        private var MAX_CHUNKS_AT_ONCE = 5
        private var currentChunkAmount = 0
    }

    fun increaseCurrentChunk() = synchronized(this) {
        currentChunkAmount++
    }

    fun decreaseCurrentChunk() = synchronized(this) {
        currentChunkAmount--
    }

    private var blockQueue = mutableListOf<QueuedBlock>()

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
    )

    private fun addBlockToQueue(_blockQueue: List<QueuedBlock>) {
        synchronized(this) {
            blockQueue.addAll(_blockQueue)
            //Проверяем максимальный размер неактивных чанков
//            if (blockQueue.size > MAX_QUEUE_SIZE)
//                blockQueue = blockQueue.drop(blockQueue.size - MAX_QUEUE_SIZE).toMutableList()

        }
    }

    private fun getQueuedBlock() = synchronized(this) {
        val block = blockQueue.firstOrNull() ?: return@synchronized null
        blockQueue.removeAt(0)
        return@synchronized block
    }


    private fun generateBlock() {
        val block = getQueuedBlock() ?: return
        if (EmpirePlugin.empireConfig.generatingDebug)
            Logger.log(
                "BlockGenerationEvent",
                "Generating block at [${block.l.x};${block.l.y};${block.l.z}] queue=${blockQueue.size}"
            )
        synchronized(this) {
            val tempChunks = EmpirePlugin.empireFiles.tempChunks
            tempChunks.getConfig().set(block.l.chunk.toString(), true)
            tempChunks.saveConfig()
        }
        callSyncMethod {
            val time = System.currentTimeMillis()
            replaceBlock(block)
            if (EmpirePlugin.empireConfig.generatingDebug)
            Logger.log("BlockGenerationEvent", "Block replacing time = ${(System.currentTimeMillis() - time)}")
        }
    }


    fun setType(block: Block, type: Material, facing: Map<String, Boolean>) {
        val craftBlock = (block as CraftBlock)
        val generatorAccess = (craftBlock.craftWorld.handle as GeneratorAccess)
        val old: IBlockData = generatorAccess.getType(craftBlock.position)
        val craftBlockData = (type.createBlockData() as CraftBlockData)
        for (f in facing)
            (craftBlockData as MultipleFacing).setFace(BlockFace.valueOf(f.key.uppercase()), f.value)

        generatorAccess.setTypeAndData(craftBlock.position, craftBlockData.state, 1042);
        generatorAccess.minecraftWorld.notify(craftBlock.position, old, craftBlockData.state, 3)
    }

    //Заменяем блок на сгенерированный
    private fun replaceBlock(b: QueuedBlock) {
        val chunkBlock = b.l.block
        setType(chunkBlock, b.m, b.f)
    }

    /**
     * Создание блоков в чанке
     */
    private fun generateChunk(chunk: Chunk) {
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
            var deposits = Random.nextInt(generate.minPerDeposit, generate.maxPerDeposit)
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
            //Записываем сгенерированное количество
            var generatedAmount = 0

            val material = BlockParser.getMaterialByData(block.data)
            val facing = BlockParser.getFacingByData(block.data)

            generate.replaceBlocks?.forEach allblocks@{ (type, chance) ->
                if (generatedAmount > deposits)
                    return@allblocks
                var minAmount = deposits / generate.replaceBlocks.size - generatedAmount
                if (minAmount < 0)
                    minAmount = 0
                if (minAmount >= deposits - generatedAmount + 1)
                    return@allblocks
                val toGenerate = Random.nextInt(minAmount, deposits - generatedAmount + 1)
                var currentBlockGeneratedAmount = 0
                (0 until toGenerate).forEach block@{ i ->
                    //Вероятность создания блока
                    if (chance < Random.nextDouble(100.0))
                        return@block
                    //Берем список локаций по текущему блоку если они существуют
                    val replaceBlocks = blockLocByType[type] ?: return@block
                    if (replaceBlocks.isEmpty())
                        return@block
                    //Берем рандомную локацию из списка локация для замены
                    val blockToReplace = replaceBlocks.elementAt(Random.nextInt(replaceBlocks.size))

                    //Берем количество в месторождении для текущей локации
                    val depositAmount = Random.nextInt(generate.minPerDeposit, generate.maxPerDeposit)
                    var faceBlock = blockToReplace.block
                    (0 until depositAmount).forEach { _ ->
                        if (generatedAmount > deposits)
                            return@allblocks
                        if (currentBlockGeneratedAmount > toGenerate)
                            return@allblocks
                        faceBlock = faceBlock.getRelative(getRandomBlockFace())
                        currentBlocksQueue.add(QueuedBlock(faceBlock.location.clone(), material, facing))
                        currentBlockGeneratedAmount++
                        generatedAmount++
                    }
                }

            }
        }
        addBlockToQueue(currentBlocksQueue)


    }

    @EventHandler
    private fun chunkLoadEvent(e: ChunkLoadEvent) {
//        if (System.currentTimeMillis() - currentChunkLoadGap < CHUNK_LOAD_GAP)
//            return
        if (currentChunkAmount > MAX_CHUNKS_AT_ONCE)
            return
        currentChunkLoadGap = System.currentTimeMillis()
        val chunk = e.chunk

        //Если чанк есть в конфиге - значит он уже генерировался
        if (EmpirePlugin.empireFiles.tempChunks.getConfig().contains(chunk.toString()))
            return

        //Проверка, включена ли генерация
        if (!e.isNewChunk && true)
            return

        runAsyncTask {
            increaseCurrentChunk()
            val start = System.currentTimeMillis()
            generateChunk(chunk)

            decreaseCurrentChunk()
        }
    }


    val task = Bukkit.getScheduler().runTaskTimer(AstraLibs.instance, Runnable {
        runAsyncTask {
            generateBlock()
        }
    }, 0L, GENERATE_BLOCK_GAP_TICK)

    override fun onDisable() {
        ChunkLoadEvent.getHandlerList().unregister(this)
        ChunkUnloadEvent.getHandlerList().unregister(this)
        PlayerJoinEvent.getHandlerList().unregister(this)
        PlayerQuitEvent.getHandlerList().unregister(this)
        task.cancel()

    }
}