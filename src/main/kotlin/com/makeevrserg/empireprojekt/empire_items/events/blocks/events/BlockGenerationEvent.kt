package com.makeevrserg.empireprojekt.empire_items.events.blocks.events

import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.empire_items.api.ItemsAPI
import com.makeevrserg.empireprojekt.empire_items.api.MushroomBlockApi
import com.makeevrserg.empireprojekt.empirelibs.ETimer
import com.makeevrserg.empireprojekt.empirelibs.IEmpireListener
import com.makeevrserg.empireprojekt.empirelibs.runAsyncTask
import net.minecraft.server.MinecraftServer
import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.event.world.ChunkUnloadEvent
import kotlin.random.Random

class BlockGenerationEvent : IEmpireListener {

    companion object{
        private val MAX_QUEUE_SIZE = 300
        private val GENERATE_BLOCK_GAP_TICK = 10L
        private val TPS_TRESHHOLD = 19.9
        private val CHUNK_LOAD_GAP = 100L
        private var currentChunkLoadGap = System.currentTimeMillis()
    }

    private var blockQueue = mutableListOf<QueuedBlock>()

    /**
     * Получаем список координат блоков, которые необходимо будет заменить
     */
    private fun Chunk.getBlocksLocations(yMin: Int, yMax: Int, types: List<String>): Map<String, List<Location>> {
        val locations = types.associateWith { mutableListOf<Location>() }

        for (y in yMin until yMax) {
            for (x in 0 until 15)
                for (z in 0 until 15) {
                    val loc = Location(world, this.x * 16.0 + x, y * 1.0, this.z * 16.0 + z)
                    if (types.contains(loc.block.type.name))
                        locations[loc.block.type.name]!!.add(loc)
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
        val f: MushroomBlockApi.Multipart
    )

    private fun addBlockToQueue(l: Location, m: Material, f: MushroomBlockApi.Multipart) {
        synchronized(this) {
            blockQueue.add(QueuedBlock(l, m, f))
        }
    }

    private fun getQueuedBlock() = synchronized(this) {
        val block = blockQueue.firstOrNull() ?: return@synchronized null
        blockQueue.removeAt(0)
        return@synchronized block
    }


    private fun generateBlock() {
        if (MinecraftServer.getServer().recentTps.firstOrNull() ?: 0.0 < TPS_TRESHHOLD)
            return
        val block = getQueuedBlock() ?: return
        println("Generating block at [${block.l.x};${block.l.y};${block.l.z}] queue=${blockQueue.size}")
        synchronized(this) {
            EmpirePlugin.empireFiles.tempChunks.getConfig().set(block.l.chunk.toString(), true)
            EmpirePlugin.empireFiles.tempChunks.saveConfig()
        }
        replaceBlock(block)
    }

    //Заменяем блок на сгенерированный
    private fun replaceBlock(b: QueuedBlock) {
        ETimer.timer("replaceBlockSingle")
        val chunkBlock = b.l.block
        chunkBlock.setType(b.m, false)
        val blockFacing = MushroomBlockApi.getMultipleFacing(chunkBlock) ?: return
        for (f in b.f.facing)
            blockFacing.setFace(BlockFace.valueOf(f.key.uppercase()), f.value)
        chunkBlock.blockData = blockFacing
        ETimer.timer("replaceBlockSingle")
    }

    /**
     * Создание блоков в чанке
     */
    private fun generateChunk(chunk: Chunk) {

        runAsyncTask {
            for ((_, block) in ItemsAPI.getEmpireBlocks()) {
                //Надо ли генерировать блок
                val generate = block.generate ?: continue
                //Если указан мир и он не равен миру чанка - пропускаем
                if (block.generate.world != null && block.generate.world != chunk.world.name)
                    continue
                //Проверяем рандом
                if (generate.generateInChunkChance < Random.nextDouble(100.0))
                    continue

                val material = MushroomBlockApi.getMaterialByData(block.data)
                val facing = MushroomBlockApi.getFacingByData(block.data)

                //Получаем максимальное количество блоков в месторождении
                val maxDeposits =
                    if (generate.maxPerDeposite >= generate.maxPerChunk) generate.maxPerChunk else generate.maxPerChunk / (generate.maxPerDeposite - generate.minPerDeposite)
                val deposits = Random.nextInt(maxDeposits)
                //Получаем список локаций блоков по их типу
                val blockLocByType =
                    chunk.getBlocksLocations(
                        generate.minY ?: 0,
                        generate.maxY ?: 20,
                        generate.replaceBlocks.keys.toList()
                    )

                if (blockLocByType.isEmpty())
                    continue
                //Записываем сгенерированное количество
                var generatedAmount = 0


                for (i in 0 until deposits) {
                    //Проверяем на максимальное количество в чанке
                    if (generatedAmount >= generate.maxPerChunk)
                        continue

                    for ((replaceBlock, chance) in generate.replaceBlocks) {
                        //Проверяем на максимальное количество в чанке
                        if (generatedAmount >= generate.maxPerChunk)
                            continue
                        //Вероятность создания блока
                        if (chance < Random.nextDouble(100.0))
                            continue
                        //Берем список локаций по текущему блоку если они существуют
                        val replaceBlocks = blockLocByType[replaceBlock] ?: continue
                        if (replaceBlocks.isEmpty())
                            continue
                        //Берем рандомную локацию из списка локация для замены
                        val blockToReplace = replaceBlocks[Random.nextInt(replaceBlocks.size)]
                        //Берем количество в месторождении для текущей локации
                        var depositeAmount = Random.nextInt(generate.minPerDeposite, generate.maxPerDeposite)
                        //Фиксим количество в месторождении
                        if (depositeAmount + generatedAmount > generate.maxPerChunk)
                            depositeAmount = generate.maxPerChunk - generatedAmount
                        //Добавляем количество в месторождении
                        generatedAmount += depositeAmount
                        var faceBlock = blockToReplace.block


                        for (i_ in 0 until depositeAmount) {
                            faceBlock = faceBlock.getRelative(getRandomBlockFace())
                            addBlockToQueue(faceBlock.location.clone(), material, facing)
                        }

                    }


                }


            }


        }
    }

    @EventHandler
    private fun chunkLoadEvent(e: ChunkLoadEvent) {
        if (System.currentTimeMillis() - currentChunkLoadGap< CHUNK_LOAD_GAP)
            return
        currentChunkLoadGap = System.currentTimeMillis()
        val chunk = e.chunk

        //Если чанк есть в конфиге - значит он уже генерировался
        if (EmpirePlugin.empireFiles.tempChunks.getConfig().contains(chunk.toString()))
            return

        //Проверка, включена ли генерация
        if (!e.isNewChunk && EmpirePlugin.empireConfig.generateOnlyOnNewChunks)
            return
        //Проверяем максимальный размер неактивных чанков
        if (blockQueue.size > MAX_QUEUE_SIZE) {
            blockQueue = blockQueue.drop(blockQueue.size - MAX_QUEUE_SIZE).toMutableList()
        }
        generateChunk(chunk)
    }



    val task = Bukkit.getScheduler().runTaskTimer(EmpirePlugin.instance, Runnable {
        generateBlock()
    }, 0L, GENERATE_BLOCK_GAP_TICK)

    override fun onDisable() {
        ChunkLoadEvent.getHandlerList().unregister(this)
        ChunkUnloadEvent.getHandlerList().unregister(this)
        PlayerJoinEvent.getHandlerList().unregister(this)
        PlayerQuitEvent.getHandlerList().unregister(this)
        task.cancel()

    }
}