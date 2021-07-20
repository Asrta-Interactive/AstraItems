package com.makeevrserg.empireprojekt.events.blocks

import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.util.EmpireUtils
import com.makeevrserg.empireprojekt.util.ResourcePackNew
import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.MultipleFacing
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.scheduler.BukkitTask
import kotlin.random.Random

class BlockGenerationEvent : Listener {


    val chunkList = mutableListOf<Chunk>()
    private val schedulers: MutableList<BukkitTask> = mutableListOf<BukkitTask>()
    val blocks = EmpirePlugin.empireItems._empireBlocks

    val chunkQueue: MutableList<Chunk> = mutableListOf()

    init {
        EmpirePlugin.instance.server.pluginManager.registerEvents(this, EmpirePlugin.instance)
    }

    public fun onDisable() {
        ChunkLoadEvent.getHandlerList().unregister(this)
        if (schedulers != null)
            for (task in schedulers)
                task.cancel()

    }


    private fun replaceBlock(chunkBlock: Block, data: Int) {
        val empireBlock = EmpirePlugin.empireItems._empireBlocksByData[data]?:return
        chunkBlock.type = MushroomBlockApi.getMaterialByData(data)
        val blockFacing = MushroomBlockApi.getMultipleFacing(chunkBlock)?:return
        val empireFacing = MushroomBlockApi.getFacingByData(data)
        for (f in empireFacing.facing)
            blockFacing.setFace(BlockFace.valueOf(f.key.uppercase()),f.value)
        chunkBlock.blockData =blockFacing
    }


    private fun GenerateBlocks(chunk: Chunk) {

        if (EmpirePlugin.config.generatingDebug)
            println("Generating blocks in ${chunk}*16. ${chunkQueue.size} chunks in queue. Current threads: ${schedulers.size}")


        val scheduler = Bukkit.getScheduler().runTaskAsynchronously(EmpirePlugin.instance, Runnable() {
            val blockMap = mutableMapOf<String, Int>()
            for (blockKey in blocks.keys) {
                val empireBlock = blocks[blockKey] ?: continue
                empireBlock.generate ?: continue
                if (empireBlock.generate.chunk <= Random.nextDouble(100.0))
                    continue

                blockMap[blockKey] = 0
                for (x in 0..15) {
                    if (blockMap[blockKey]!! > empireBlock.generate.maxPerChunk)
                        break

                    for (z in 0..15) {
                        if (blockMap[blockKey]!! > empireBlock.generate.maxPerChunk)
                            break
                        for (y in empireBlock.generate.minY..empireBlock.generate.maxY) {
                            if (blockMap[blockKey]!! > empireBlock.generate.maxPerChunk)
                                break

                            val location = Location(chunk.world, x + chunk.x * 16.0, y.toDouble(), z + chunk.z * 16.0)
                            val chunkBlock = chunk.world.getBlockAt(location)
                            if (!empireBlock.generate.replaceBlocks.contains(chunkBlock.type))
                                continue

                            if (empireBlock.generate.replaceBlocks[chunkBlock.type] ?: continue
                                <= Random.nextDouble(100.0)
                            )
                                break

                            synchronized(EmpirePlugin.instance) {
                                Bukkit.getScheduler().callSyncMethod(EmpirePlugin.instance) {
                                    val depositAmount = Random.nextInt(
                                        empireBlock.generate.minDeposite,
                                        empireBlock.generate.maxDeposite
                                    )
                                    for (i in 0 until depositAmount) {
                                        val block: Block = chunkBlock.getRelative(BlockFace.UP)
                                        if (blockMap[blockKey]!! > empireBlock.generate.maxPerChunk)
                                            break
                                        blockMap[blockKey] = blockMap[blockKey]!! + 1
                                        replaceBlock(block, empireBlock.data)
                                    }
                                }
                            }

                        }


                    }
                }
            }

            synchronized(EmpirePlugin.instance) {
                val tasksToRemove = mutableListOf<BukkitTask>()
                for (task in schedulers.toList()) {
                    val id = task.taskId
                    if (!Bukkit.getScheduler().pendingTasks.contains(task))
                        tasksToRemove.add(task)
                }
                for (task in tasksToRemove)
                    schedulers.remove(task)

                if (schedulers.size < 10 && chunkQueue.isNotEmpty()) {
                    GenerateBlocks(chunkQueue[0])
                    chunkQueue.removeAt(0)
                }

            }

        })
        schedulers.add(scheduler)
    }


    @EventHandler
    private fun ChunkEvent(e: ChunkLoadEvent) {
        //println("${e.eventName} ${e.isNewChunk}")
        val chunk = e.chunk

        if (!e.isNewChunk)
            return
        if (schedulers.size > 10) {
            chunkQueue.add(chunk)
            return
        }
        if (chunkList.contains(chunk))
            return
        chunkList.add(chunk)
        GenerateBlocks(chunk)


    }


}