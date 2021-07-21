package com.makeevrserg.empireprojekt.events.empireevents

import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.EmpirePlugin.Companion.instance
import com.makeevrserg.empireprojekt.util.EmpireUtils
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType

class LavaWalkerEvent : Listener {
    init {
        instance.server.pluginManager.registerEvents(this, instance)
    }

    fun onDisable() {
        PlayerMoveEvent.getHandlerList().unregister(this)
        EntityDamageEvent.getHandlerList().unregister(this)
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
    fun playerFireEvent(e: EntityDamageEvent) {
        if (e.cause != EntityDamageEvent.DamageCause.FIRE && e.cause != EntityDamageEvent.DamageCause.FIRE_TICK && e.cause != EntityDamageEvent.DamageCause.LAVA)
            return
        if (e.entity !is Player)
            return
        val player = e.entity as Player
        val equipment = player.equipment ?: return
        if (allMagmaSet(equipment.armorContents)) {
            e.damage = 0.0
            player.fireTicks = 0
            e.isCancelled = true
        }

    }

    private fun allMagmaSet(armorContents: Array<ItemStack>): Boolean {
        for (item in armorContents)
            if (!hasLaveWalker(item))
                return false
        return true
    }

    private fun hasLaveWalker(item: ItemStack?): Boolean {
        return hasLaveWalker(item?.itemMeta)
    }

    private fun hasLaveWalker(meta: ItemMeta?): Boolean {
        meta ?: return false
        return meta.persistentDataContainer
            .has(EmpirePlugin.empireConstants.LAVA_WALKER_ENCHANT, PersistentDataType.DOUBLE)
    }

    @EventHandler
    private fun playerMoveEvent(e: PlayerMoveEvent) {
        val itemStack = e.player.inventory.boots ?: return
        val itemMeta = itemStack.itemMeta ?: return
        if (!hasLaveWalker(itemMeta)) return
        val onToBlock = e.to?.block?.getRelative(BlockFace.DOWN) ?: return
        if (allMagmaSet(e.player.equipment?.armorContents ?: return))
            if (onToBlock.type == Material.LAVA)
                createBlocks(onToBlock)

    }

}