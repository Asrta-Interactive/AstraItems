package com.astrainteractive.empire_items.empire_items.events.empireevents

import com.astrainteractive.astralibs.EventListener
import com.astrainteractive.empire_items.empire_items.api.items.BlockParser
import com.astrainteractive.empire_items.empire_items.api.utils.BukkitConstants
import com.astrainteractive.empire_items.empire_items.api.utils.hasPersistentData
import com.astrainteractive.empire_items.empire_items.util.AsyncHelper
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

class LavaWalkerEvent : EventListener {


    override fun onDisable() {
        PlayerMoveEvent.getHandlerList().unregister(this)
        EntityDamageEvent.getHandlerList().unregister(this)
    }

    fun Block.setTypeFast(type: Material) =
        BlockParser.setTypeFast(this, type)


    private fun createBlocks(block: Block) {
        block.setTypeFast(Material.COBBLESTONE)
        block.getRelative(BlockFace.EAST).setTypeFast(Material.COBBLESTONE)
        block.getRelative(BlockFace.WEST).setTypeFast(Material.COBBLESTONE)
        block.getRelative(BlockFace.SOUTH).setTypeFast(Material.COBBLESTONE)
        block.getRelative(BlockFace.SOUTH_EAST).setTypeFast(Material.COBBLESTONE)
        block.getRelative(BlockFace.SOUTH_WEST).setTypeFast(Material.COBBLESTONE)
        block.getRelative(BlockFace.NORTH).setTypeFast(Material.COBBLESTONE)
        block.getRelative(BlockFace.NORTH_EAST).setTypeFast(Material.COBBLESTONE)
        block.getRelative(BlockFace.NORTH_WEST).setTypeFast(Material.COBBLESTONE)
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

    private fun hasLaveWalker(meta: ItemMeta?): Boolean =
        meta?.hasPersistentData(BukkitConstants.EmpireEnchants.LAVA_WALKER) == true


    @EventHandler
    private fun playerMoveEvent(e: PlayerMoveEvent) {
        val itemStack = e.player.inventory.boots ?: return
        val itemMeta = itemStack.itemMeta ?: return
        AsyncHelper.runBackground {

            if (!hasLaveWalker(itemMeta)) return@runBackground
            val onToBlock = e.to.block.getRelative(BlockFace.DOWN)
//        if (allMagmaSet(e.player.equipment?.armorContents ?: return))
            if (onToBlock.type == Material.LAVA)
                createBlocks(onToBlock)
        }

    }

}
