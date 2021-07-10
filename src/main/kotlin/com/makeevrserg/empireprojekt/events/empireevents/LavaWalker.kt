package com.makeevrserg.empireprojekt.events.empireevents

import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.EmpirePlugin.Companion.instance
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.persistence.PersistentDataType

class LavaWalker : Listener {
    init {
        instance.server.pluginManager.registerEvents(this, instance)
    }

    fun onDisable() {
        PlayerMoveEvent.getHandlerList().unregister(this)
    }

    private fun createBlocks(block: Block) {
        block.type = Material.COBBLESTONE
        block.getRelative(BlockFace.EAST).type = Material.COBBLESTONE
        block.getRelative(BlockFace.WEST).type = Material.COBBLESTONE
        block.getRelative(BlockFace.SOUTH).type = Material.COBBLESTONE
        block.getRelative(BlockFace.SOUTH_EAST).type = Material.COBBLESTONE
        block.getRelative(BlockFace.SOUTH_WEST).type = Material.COBBLESTONE
        block.getRelative(BlockFace.NORTH).type = Material.COBBLESTONE
        block.getRelative(BlockFace.NORTH_EAST).type = Material.COBBLESTONE
        block.getRelative(BlockFace.NORTH_WEST).type = Material.COBBLESTONE
    }

    @EventHandler
    private fun playerMoveEvent(e: PlayerMoveEvent) {
        val itemStack = e.player.inventory.boots ?: return
        val itemMeta = itemStack.itemMeta ?: return
        if (!itemMeta.persistentDataContainer
                .has(EmpirePlugin.empireConstants.LAVA_WALKER_ENCHANT, PersistentDataType.DOUBLE)
        ) return
        val onToBlock = e.to?.block?.getRelative(BlockFace.DOWN)?:return
        if (onToBlock.type == Material.LAVA) {
            createBlocks(onToBlock)
        }
    }

}
