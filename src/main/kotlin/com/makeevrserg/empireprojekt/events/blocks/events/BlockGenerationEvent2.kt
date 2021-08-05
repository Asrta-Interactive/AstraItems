package com.makeevrserg.empireprojekt.events.blocks

import com.makeevrserg.empireprojekt.EmpirePlugin
import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.event.world.ChunkUnloadEvent
import org.bukkit.scheduler.BukkitTask
import kotlin.random.Random

class BlockGenerationEvent2 : Listener {


    val blocks = EmpirePlugin.empireItems._empireBlocks

    val activeChunks = mutableListOf<Chunk>()
    val inactiveChunks = mutableListOf<Chunk>()

    val activeTasks = mutableListOf<BukkitTask>()


    init {
        EmpirePlugin.instance.server.pluginManager.registerEvents(this, EmpirePlugin.instance)
    }

    public fun onDisable() {
        ChunkLoadEvent.getHandlerList().unregister(this)
        ChunkUnloadEvent.getHandlerList().unregister(this)
        for (task in activeTasks)
            task.cancel()
    }


    private fun Chunk.getNearBlocks(
        yMin: Int,
        yMax: Int,
        types: List<Material>
    ): MutableMap<Material, MutableList<Location>> {
        val locations = mutableMapOf<Material, MutableList<Location>>()
        for (type in types)
            locations[type] = mutableListOf()

        for (y in yMin until yMax) {
            for (x in 0 until 15) {
                for (z in 0 until 15) {
                    val loc = Location(world, this.x * 16.0 + x, y * 1.0, this.z * 16.0 + z)
                    if (types.contains(loc.block.type))
                        locations[loc.block.type]!!.add(loc)
                }
            }

        }
        return locations
    }

    private fun replaceBlock(chunkBlock: Block,material: Material,facing:MushroomBlockApi.Multipart) {
        chunkBlock.type = material
        val blockFacing = MushroomBlockApi.getMultipleFacing(chunkBlock) ?: return
        for (f in facing.facing)
            blockFacing.setFace(BlockFace.valueOf(f.key.uppercase()), f.value)
        chunkBlock.blockData = blockFacing
    }

    private fun getRandomBlockFace(): BlockFace {
        val vals = BlockFace.values()
        return vals[Random.nextInt(vals.size)]
    }

    private fun generateChunk(chunk: Chunk) {

        EmpirePlugin.empireFiles.tempChunks.getConfig()?.set(chunk.toString(), true) ?: return
        EmpirePlugin.empireFiles.tempChunks.saveConfig()

        val task = Bukkit.getScheduler().runTaskAsynchronously(EmpirePlugin.instance, Runnable {
            if (EmpirePlugin.config.generatingDebug) {
                println("Generating blocks in ${chunk}*16. ${inactiveChunks.size} chunks in queue. Current chunks: ${activeChunks.size}; Current Threads: ${activeTasks.size}")
            }

            for ((id, block) in blocks) {
                val generate = block.generate ?: continue
                if (block.generate.world!= null && block.generate.world!=chunk.world.name) {
                    continue
                }
                if (generate.chunk < Random.nextDouble(100.0))
                    continue

                val material = MushroomBlockApi.getMaterialByData(block.data)
                val facing = MushroomBlockApi.getFacingByData(block.data)


                val maxDeposits =
                    if (generate.maxDeposite > generate.maxPerChunk) generate.maxPerChunk else generate.maxPerChunk / (generate.maxDeposite - generate.minDeposite)

                val deposits = Random.nextInt(maxDeposits)
                val blockLocByType =
                    chunk.getNearBlocks(generate.minY, generate.maxY, generate.replaceBlocks.keys.toList())

                if (blockLocByType.isEmpty())
                    continue
                var generatedAmount = 0

                for (i in 0 until deposits) {
                    if (generatedAmount >= generate.maxPerChunk)
                        continue
                    for ((replaceBlock, chance) in generate.replaceBlocks) {
                        if (generatedAmount >= generate.maxPerChunk)
                            continue
                        if (chance < Random.nextDouble(100.0))
                            continue
                        val replaceBlocks = blockLocByType[replaceBlock] ?: continue
                        if (replaceBlocks.isEmpty())
                            continue

                        val blockToReplace = replaceBlocks[Random.nextInt(replaceBlocks.size)] ?: continue
                        var depositeAmount = Random.nextInt(generate.minDeposite, generate.maxDeposite)
                        if (depositeAmount + generatedAmount > generate.maxPerChunk)
                            depositeAmount = generate.maxPerChunk - generatedAmount

                        generatedAmount += depositeAmount
                        var faceBlock = blockToReplace.block
                        Bukkit.getScheduler().callSyncMethod(EmpirePlugin.instance) {
                            for (i_ in 0 until depositeAmount) {
                                faceBlock = faceBlock.getRelative(getRandomBlockFace())
                                replaceBlock(faceBlock, material,facing)

                            }
                        }

                    }


                }


            }
            synchronized(this) {
                activeChunks.remove(chunk)
                if (inactiveChunks.isNotEmpty()) {
                    val newChunk = inactiveChunks[0]
                    inactiveChunks.removeAt(0)
                    activeChunks.add(newChunk)
                    generateChunk(newChunk)
                }
                for (task in activeTasks.toList())
                    if (!Bukkit.getScheduler().pendingTasks.contains(task))
                        activeTasks.remove(task)
            }
        })
        activeTasks.add(task)


    }
    @EventHandler
    private fun chunkUnloadEvent(e:ChunkUnloadEvent){
        inactiveChunks.remove(e.chunk)
    }

    @EventHandler
    private fun ChunkEvent(e: ChunkLoadEvent) {
        val chunk = e.chunk



        if (EmpirePlugin.empireFiles.tempChunks.getConfig()?.contains(chunk.toString()) ?: return)
            return

        if (!e.isNewChunk && EmpirePlugin.config.generateOnlyOnNewChunks)
            return
        if (activeChunks.size < 3) {
            activeChunks.add(chunk)
            generateChunk(chunk)
        } else
            inactiveChunks.add(chunk)


    }


}