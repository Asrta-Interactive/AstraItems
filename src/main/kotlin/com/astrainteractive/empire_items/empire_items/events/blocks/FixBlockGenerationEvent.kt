package com.astrainteractive.empire_items.empire_items.events.blocks


import com.astrainteractive.astralibs.*
import com.astrainteractive.empire_items.EmpirePlugin
import com.astrainteractive.empire_items.empire_items.api.items.BlockParser
import com.astrainteractive.empire_items.empire_items.events.blocks.GenerationUtils.containsPlayers
import com.astrainteractive.empire_items.empire_items.events.blocks.GenerationUtils.distanceToPlayer
import com.astrainteractive.empire_items.empire_items.util.TriplePair
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.scheduler.BukkitTask
import kotlin.coroutines.CoroutineContext

class FixBlockGenerationEvent : EventListener, CoroutineScope {
    private val job: Job
        get() = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Default

    private val TAG: String
        get() = "BlockGenerationEvent"



    /**
     * Получаем список координат блоков, которые необходимо будет заменить
     */
    private fun Chunk.getBlocksLocations(
        yMin: Int,
        yMax: Int
    ): List<Location> {
        val xMap = 0 until 16
        val yMap = (yMin until yMax)
        val singleMap = yMap.flatMap { y ->
            return@flatMap xMap.flatMap f2@{ x ->
                return@f2 xMap.map { z ->
                    return@map TriplePair(x, y, z)
                }
            }
        }

        return singleMap.mapNotNull { (x, y, z) ->
            val loc = Location(world, this.x * 16.0 + x, y * 1.0, this.z * 16.0 + z * 1)
            val blockName = loc.block.type
            if (blockName != Material.BROWN_MUSHROOM_BLOCK)
                return@mapNotNull null
            loc
        }
    }

    @EventHandler
    private fun interact(e: PlayerInteractEvent) {
        if (e.action != Action.RIGHT_CLICK_BLOCK)
            return
        val chunk = e.player.location.chunk
        val list = chunk.getBlocksLocations(-40, 50)
        println(" Chunk=${chunk} ${list.size} blocks")

        list.forEach {


//            Bukkit.getScheduler().runTaskLaterAsynchronously(EmpirePlugin.instance, Runnable {
//                BlockParser.setTypeFast(it.block, Material.AIR)
//            }, 2L)
        }
    }

    @EventHandler
    private fun chunkLoadEvent(e: ChunkLoadEvent) {
        val chunk = e.chunk

    }

    fun Chunk.hasPlayers(): Boolean {
        val list = Bukkit.getOnlinePlayers().filter { this.distanceToPlayer(it) < Bukkit.getServer().viewDistance }
        return list.isNotEmpty()
    }

    lateinit var task: BukkitTask
    override fun onEnable(manager: EventManager): EventListener {


        val tempChunks = EmpirePlugin.empireFiles.tempChunks.getConfig()
        task = Bukkit.getScheduler().runTaskAsynchronously(EmpirePlugin.instance, Runnable {
            val keys = tempChunks.getKeys(false)
            val size = keys.size
            var i = 0
            for (_key in keys) {
                val key =
                    _key.replace("CraftChunk{", "").replace("}", "").replace(" ", "").replace("z", " z")
                        .replace("x=", "")
                        .replace("z=", "")
                val array = key.split(" ")
                val x = array.getOrNull(0)?.toIntOrNull() ?: continue
                val z = array.getOrNull(1)?.toIntOrNull() ?: continue
                var chunk = Bukkit.getWorld("world")?.getChunkAt(x, z) ?: continue
                var locations = chunk.getBlocksLocations(-100, 100).toMutableList()
                chunk = Bukkit.getWorld("world_the_end")?.getChunkAt(x, z) ?: continue
                locations.addAll(chunk.getBlocksLocations(-10, 50))
                if (locations.isNullOrEmpty()) {
                    tempChunks.set("${chunk}", null)
                }

                locations.forEach {
                    tempChunks.set("${chunk}", null)
                    Bukkit.getScheduler().runTaskLaterAsynchronously(EmpirePlugin.instance, Runnable {
                        BlockParser.setTypeFast(it.block, Material.AIR)
                    }, 2L)
                }

                println("_key=${_key} x=${x} z=${z} Chunk=${chunk} ${locations.size} blocks; Progress: ${i++}/${size}")
                if (i % 100 == 0) synchronized(this) {
                    EmpirePlugin.empireFiles.tempChunks.saveConfig()
                }

            }
        })

        return super.onEnable(manager)
    }

    override fun onDisable() {
        task.cancel()

    }

}

