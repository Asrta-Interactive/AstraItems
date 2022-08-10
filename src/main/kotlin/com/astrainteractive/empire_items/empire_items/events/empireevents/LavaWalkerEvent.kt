package com.astrainteractive.empire_items.empire_items.events.empireevents

import com.astrainteractive.astralibs.events.EventListener
import com.astrainteractive.astralibs.async.AsyncHelper
import com.astrainteractive.astralibs.events.DSLEvent
import com.astrainteractive.empire_items.api.enchants.EmpireEnchants
import com.astrainteractive.empire_items.api.items.BlockParser
import com.astrainteractive.empire_items.api.utils.BukkitConstants
import com.astrainteractive.empire_items.api.utils.hasPersistentData
import kotlinx.coroutines.launch
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

class LavaWalkerEvent{

    fun Block.setTypeFast(type: Material) =
        AsyncHelper.launch { BlockParser.setTypeFast(this@setTypeFast, type) }


    private fun Block.lavaOrNull() = if (this.type == Material.LAVA) this else null

    private fun createBlocks(block: Block) {
        block.setTypeFast(Material.COBBLESTONE)
        block.getRelative(BlockFace.EAST).lavaOrNull()?.setTypeFast(Material.COBBLESTONE)
        block.getRelative(BlockFace.WEST).lavaOrNull()?.setTypeFast(Material.COBBLESTONE)
        block.getRelative(BlockFace.SOUTH).lavaOrNull()?.setTypeFast(Material.COBBLESTONE)
        block.getRelative(BlockFace.SOUTH_EAST).lavaOrNull()?.setTypeFast(Material.COBBLESTONE)
        block.getRelative(BlockFace.SOUTH_WEST).lavaOrNull()?.setTypeFast(Material.COBBLESTONE)
        block.getRelative(BlockFace.NORTH).lavaOrNull()?.setTypeFast(Material.COBBLESTONE)
        block.getRelative(BlockFace.NORTH_EAST).lavaOrNull()?.setTypeFast(Material.COBBLESTONE)
        block.getRelative(BlockFace.NORTH_WEST).lavaOrNull()?.setTypeFast(Material.COBBLESTONE)
    }

    val playerFireEvent = DSLEvent.event(EntityDamageEvent::class.java)  { e ->
        if (e.cause != EntityDamageEvent.DamageCause.FIRE && e.cause != EntityDamageEvent.DamageCause.FIRE_TICK && e.cause != EntityDamageEvent.DamageCause.LAVA)
            return@event
        if (e.entity !is Player)
            return@event
        val player = e.entity as Player
        val equipment = player.equipment ?: return@event
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
        meta?.hasPersistentData(EmpireEnchants.LAVA_WALKER_ENCHANT) == true


    val playerMoveEvent = DSLEvent.event(PlayerMoveEvent::class.java)  { e ->
        val itemStack = e.player.inventory.boots ?: return@event
        val itemMeta = itemStack.itemMeta ?: return@event
        AsyncHelper.launch {

            if (!hasLaveWalker(itemMeta)) return@launch
            val onToBlock = e.to.block.getRelative(BlockFace.DOWN)

            if (onToBlock.type == Material.LAVA)
                createBlocks(onToBlock)
        }

    }

}
