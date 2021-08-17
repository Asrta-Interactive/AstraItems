package com.makeevrserg.empireprojekt.events.empireevents

import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.EmpirePlugin.Companion.instance
import com.makeevrserg.empireprojekt.util.BetterConstants
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.world.World
import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.bukkit.WorldGuardPlugin
import com.sk89q.worldguard.protection.flags.Flag
import com.sk89q.worldguard.protection.flags.Flags
import com.sk89q.worldguard.protection.regions.RegionQuery
import empirelibs.IEmpireListener
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.persistence.PersistentDataType
import java.util.*

class HammerEvent : IEmpireListener {
    private val blockFace: MutableMap<Player, Int> = mutableMapOf()




    override fun onDisable() {
        PlayerInteractEvent.getHandlerList().unregister(this)
        BlockBreakEvent.getHandlerList().unregister(this)
    }

    @EventHandler
    fun playerInteractEvent(e: PlayerInteractEvent) {
        val p = e.player
        val bFace = e.blockFace
        val side: Int
        val strside = bFace.name
        side = if (strside.equals("up", ignoreCase = true) || strside.equals(
                "down",
                ignoreCase = true
            )
        ) 0 else if (strside.equals("south", ignoreCase = true) || strside.equals("north", ignoreCase = true)) 1 else 2
        blockFace[p] = side
    }

    @EventHandler
    fun playerBreakEvent(e: BlockBreakEvent) {
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
                    return
                }
        }
        val itemStack = e.player.inventory.itemInMainHand
        val itemMeta = itemStack.itemMeta ?: return
        if (!itemMeta.persistentDataContainer
                .has(BetterConstants.HAMMER_ENCHANT.value, PersistentDataType.DOUBLE)
        ) return
        val side = blockFace[e.player]
        blockFace.remove(e.player)
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
