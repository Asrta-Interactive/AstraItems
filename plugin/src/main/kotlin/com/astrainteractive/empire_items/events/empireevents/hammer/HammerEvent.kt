package com.astrainteractive.empire_items.events.empireevents.hammer

import com.astrainteractive.empire_itemss.api.utils.BukkitConstants
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent
import ru.astrainteractive.astralibs.events.DSLEvent
import ru.astrainteractive.astralibs.utils.persistence.Persistence.hasPersistentData
import java.util.*

class HammerEvent {
    private val blockFace: MutableMap<UUID, Int> = HashMap()

    val playerQuitEvent = DSLEvent.event<PlayerQuitEvent> { e ->
        blockFace.remove(e.player.uniqueId)
    }

    val playerInteractEvent = DSLEvent.event<PlayerInteractEvent> { e ->
        val side = when (e.blockFace) {
            BlockFace.UP, BlockFace.DOWN -> 0
            BlockFace.SOUTH, BlockFace.NORTH -> 1
            else -> 2
        }
        blockFace[e.player.uniqueId] = side
    }

    val playerBreakEvent = DSLEvent.event<BlockBreakEvent> { e ->
        val p = e.player
        val b = e.block
        if (e.isCancelled) return@event

        val itemStack = e.player.inventory.itemInMainHand
        val itemMeta = itemStack.itemMeta ?: return@event
        if (itemMeta.hasPersistentData(BukkitConstants.HAMMER_ENCHANT) != true) return@event


        val side = blockFace.remove(e.player.uniqueId) ?: return@event
        if (e.isCancelled) return@event

        createBlocksList(side, b).forEach { block ->
            val blockName = block.type.name
            PICKAXE_BLOCK_KEYS.firstOrNull { blockName.contains(it, ignoreCase = true) } ?: return@forEach
            p.breakBlock(block)
        }

    }

    companion object {
        private val PICKAXE_BLOCK_KEYS: HashSet<String> = hashSetOf(
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
    }


    private fun createBlocksList(side: Int, b: Block): List<Block> = when (side) {
        0 -> buildList {
            add(b.getRelative(BlockFace.NORTH))
            add(b.getRelative(BlockFace.SOUTH))
            add(b.getRelative(BlockFace.WEST))
            add(b.getRelative(BlockFace.EAST))
            add(b.getRelative(BlockFace.NORTH_WEST))
            add(b.getRelative(BlockFace.NORTH_EAST))
            add(b.getRelative(BlockFace.SOUTH_EAST))
            add(b.getRelative(BlockFace.SOUTH_WEST))
        }

        1 -> buildList {
            add(b.getRelative(BlockFace.WEST))
            add(b.getRelative(BlockFace.EAST))
            add(b.getRelative(BlockFace.UP))
            add(b.getRelative(BlockFace.DOWN))
            add(b.getRelative(BlockFace.UP).getRelative(BlockFace.WEST))
            add(b.getRelative(BlockFace.UP).getRelative(BlockFace.EAST))
            add(b.getRelative(BlockFace.DOWN).getRelative(BlockFace.WEST))
            add(b.getRelative(BlockFace.DOWN).getRelative(BlockFace.EAST))
        }

        else -> buildList {
            add(b.getRelative(BlockFace.SOUTH))
            add(b.getRelative(BlockFace.NORTH))
            add(b.getRelative(BlockFace.UP))
            add(b.getRelative(BlockFace.DOWN))
            add(b.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH))
            add(b.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH))
            add(b.getRelative(BlockFace.DOWN).getRelative(BlockFace.SOUTH))
            add(b.getRelative(BlockFace.DOWN).getRelative(BlockFace.NORTH))
        }
    }

}
