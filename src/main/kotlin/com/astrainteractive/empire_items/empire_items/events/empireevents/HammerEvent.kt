package com.astrainteractive.empire_items.empire_items.events.empireevents

import com.astrainteractive.astralibs.events.DSLEvent
import com.astrainteractive.astralibs.events.EventListener
import com.astrainteractive.empire_items.EmpirePlugin.Companion.instance
import com.astrainteractive.empire_items.api.utils.BukkitConstants
import com.astrainteractive.empire_items.api.utils.hasPersistentData
import com.astrainteractive.empire_items.empire_items.util.CleanerTask
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.world.World
import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.bukkit.WorldGuardPlugin
import com.sk89q.worldguard.protection.flags.Flags
import com.sk89q.worldguard.protection.regions.RegionQuery
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.player.PlayerInteractEvent
import java.util.*

class HammerEvent{
    private val blockFace: MutableMap<UUID, Int> = mutableMapOf()
    val cleaner = CleanerTask(50000) {
        blockFace.clear()
    }
    val playerInteractEvent = DSLEvent.event(PlayerInteractEvent::class.java)  { e ->
        val p = e.player
        val bFace = e.blockFace
        val side: Int
        val strside = bFace.name
        side = if (strside.equals("up", ignoreCase = true) || strside.equals(
                "down",
                ignoreCase = true
            )
        ) 0 else if (strside.equals("south", ignoreCase = true) || strside.equals("north", ignoreCase = true)) 1 else 2
        blockFace[p.uniqueId] = side
    }

    val playerBreakEvent = DSLEvent.event(BlockBreakEvent::class.java)  { e ->
        val p = e.player
        val b = e.block
        if (instance.server.pluginManager.getPlugin("WorldGuard") != null) {
            val query: RegionQuery = WorldGuard.getInstance().platform.regionContainer.createQuery()
            val loc: com.sk89q.worldedit.util.Location = BukkitAdapter.adapt(b.location)
            val world: World = BukkitAdapter.adapt(b.world)
            if (!WorldGuard.getInstance().platform.sessionManager
                    .hasBypass(WorldGuardPlugin.inst().wrapPlayer(p), world)
            )
                if (!query.testState(loc, WorldGuardPlugin.inst().wrapPlayer(p), Flags.BUILD,Flags.BLOCK_BREAK)) {
                    e.isCancelled = true
                    return@event
                }
        }
        val itemStack = e.player.inventory.itemInMainHand
        val itemMeta = itemStack.itemMeta ?: return@event
        if (itemMeta.hasPersistentData(BukkitConstants.HAMMER_ENCHANT)!=true) return@event
        val side = blockFace[e.player.uniqueId]
        blockFace.remove(e.player.uniqueId)
        if (side != null) {
            val blocks: MutableList<Block> =
                ArrayList()
            when (side) {
                0 -> {
                    blocks.add(b.getRelative(BlockFace.NORTH))
                    blocks.add(b.getRelative(BlockFace.SOUTH))
                    blocks.add(b.getRelative(BlockFace.WEST))
                    blocks.add(b.getRelative(BlockFace.EAST))
                    blocks.add(b.getRelative(BlockFace.NORTH_WEST))
                    blocks.add(b.getRelative(BlockFace.NORTH_EAST))
                    blocks.add(b.getRelative(BlockFace.SOUTH_EAST))
                    blocks.add(b.getRelative(BlockFace.SOUTH_WEST))
                }
                1 -> {
                    blocks.add(b.getRelative(BlockFace.WEST))
                    blocks.add(b.getRelative(BlockFace.EAST))
                    blocks.add(b.getRelative(BlockFace.UP))
                    blocks.add(b.getRelative(BlockFace.DOWN))
                    blocks.add(b.getRelative(BlockFace.UP).getRelative(BlockFace.WEST))
                    blocks.add(b.getRelative(BlockFace.UP).getRelative(BlockFace.EAST))
                    blocks.add(b.getRelative(BlockFace.DOWN).getRelative(BlockFace.WEST))
                    blocks.add(b.getRelative(BlockFace.DOWN).getRelative(BlockFace.EAST))
                }
                else -> {
                    blocks.add(b.getRelative(BlockFace.SOUTH))
                    blocks.add(b.getRelative(BlockFace.NORTH))
                    blocks.add(b.getRelative(BlockFace.UP))
                    blocks.add(b.getRelative(BlockFace.DOWN))
                    blocks.add(b.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH))
                    blocks.add(b.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH))
                    blocks.add(b.getRelative(BlockFace.DOWN).getRelative(BlockFace.SOUTH))
                    blocks.add(b.getRelative(BlockFace.DOWN).getRelative(BlockFace.NORTH))
                }
            }
            for (block in blocks) {
                val blockName = block.type.toString().toLowerCase()
                //shovel
                //pickaxe
                val keys = listOf(
                    "stone",
                    "deepslate",
                    "granite",
                    "diorite",
                    "andesite",
                    "ore",
                    "block",
                    "cobblestone",
                    "sandstone",
                    "purpur",
                    "prismarine",
                    "brick",
                    "bars",
                    "chain",
                    "terracotta",
                    "quartz",
                    "ice",
                    "magma",
                    "concrete",
                    "blackstone",
                    "netherrack",
                    "basalt"
                )
                for (key in keys) if (blockName.contains(key)) {
                    p.breakBlock(block)
                    //block.breakNaturally()
                    break
                }
            }
        }
    }


}
