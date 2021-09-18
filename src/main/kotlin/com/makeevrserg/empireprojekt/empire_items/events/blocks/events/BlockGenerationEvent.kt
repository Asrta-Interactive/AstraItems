package com.makeevrserg.empireprojekt.empire_items.events.blocks.events

import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.empire_items.api.MushroomBlockApi
import com.makeevrserg.empireprojekt.empirelibs.IEmpireListener
import com.makeevrserg.empireprojekt.empirelibs.callSyncMethod
import com.makeevrserg.empireprojekt.empirelibs.runAsyncTask
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
import org.bukkit.plugin.IllegalPluginAccessException
import org.bukkit.scheduler.BukkitTask
import kotlin.random.Random

class BlockGenerationEvent : IEmpireListener {

    val blocks = EmpirePlugin.empireItems.empireBlocks
    private val activeChunks = mutableListOf<Chunk>()
    private var inactiveChunks = mutableListOf<Chunk>()
    private val activeTasks = mutableListOf<BukkitTask>()

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

    //Заменяем блок на сгенерированный
    private fun replaceBlock(blockLoc: Location, material: Material, facing: MushroomBlockApi.Multipart) {

        callSyncMethod {
            val chunkBlock = blockLoc.block
            chunkBlock.type = material
            val blockFacing = MushroomBlockApi.getMultipleFacing(chunkBlock) ?: return@callSyncMethod
            for (f in facing.facing)
                blockFacing.setFace(BlockFace.valueOf(f.key.uppercase()), f.value)
            chunkBlock.blockData = blockFacing
        }


    }

    /**
     * Создание блоков в чанке
     */
    private fun generateChunk(chunk: Chunk) {

        //Добавляем чанк в список сгенериированных
        synchronized(this) {
            EmpirePlugin.empireFiles.tempChunks.getConfig().set(chunk.toString(), true)
            EmpirePlugin.empireFiles.tempChunks.saveConfig()
        }


        val task = runAsyncTask {
            //Если включен дебаг - Отправляем сообщение на сервер о нынешнем генерированном чанке
            if (EmpirePlugin.empireConfig.generatingDebug)
                println("Generating blocks in ${chunk}*16. ${inactiveChunks.size} chunks in queue. Current chunks: ${activeChunks.size}; Current Threads: ${activeTasks.size}")


            for ((_, block) in blocks) {
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

                //todo Здесь что-то не так. Надо пересмотреть, как можно переделать циклы
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
                            replaceBlock(faceBlock.location.clone(), material, facing)

                        }


                    }


                }


            }
            removeChunkFromTask(chunk)
        }
        activeTasks.add(task ?: return)


    }

    private fun removeChunkFromTask(chunk: Chunk) {
        runAsyncTask {
            synchronized(this) {
                inactiveChunks.remove(chunk)
                activeChunks.remove(chunk)
                if (inactiveChunks.isNotEmpty()) {
                    val newChunk = inactiveChunks.elementAt(0)
                    inactiveChunks.removeAt(0)
                    if (activeChunks.size < 5) {
                        activeChunks.add(newChunk)
                        generateChunk(newChunk)
                    }
                }
                for (task in activeTasks.toList())
                    if (!Bukkit.getScheduler().pendingTasks.contains(task))
                        activeTasks.remove(task)
            }
        }
    }

    @EventHandler
    private fun chunkUnloadEvent(e: ChunkUnloadEvent) {

        removeChunkFromTask(e.chunk)


    }

    @EventHandler
    private fun chunkLoadEvent(e: ChunkLoadEvent) {
        val chunk = e.chunk

        //Если чанк есть в конфиге - значит он уже генерировался
        if (EmpirePlugin.empireFiles.tempChunks.getConfig().contains(chunk.toString()))
            return

        //Проверка, включена ли генерация
        if (!e.isNewChunk && EmpirePlugin.empireConfig.generateOnlyOnNewChunks)
            return
        //Проверяем максимальный размер неактивных чанков
        if (inactiveChunks.size > 300) {
            inactiveChunks = inactiveChunks.drop(200).toMutableList()
            return
        }
        //Проверяем размер активных чанков
        if (activeChunks.size < 2) {
            activeChunks.add(chunk)
            generateChunk(chunk)
        } else
            inactiveChunks.add(chunk)


    }

    @EventHandler
    fun playerJoinEvent(e: PlayerJoinEvent) {
        removeChunkFromTask(e.player.location.chunk)

    }

    @EventHandler
    fun playerJoinEvent(e: PlayerQuitEvent) {

                removeChunkFromTask(e.player.location.chunk)

    }

    override fun onDisable() {
        ChunkLoadEvent.getHandlerList().unregister(this)
        ChunkUnloadEvent.getHandlerList().unregister(this)
        PlayerJoinEvent.getHandlerList().unregister(this)
        PlayerQuitEvent.getHandlerList().unregister(this)
        if (activeChunks.isNotEmpty())
            for (chunk in activeChunks.toList())
                removeChunkFromTask(chunk)

        for (task in activeTasks)
            task.cancel()
    }
}