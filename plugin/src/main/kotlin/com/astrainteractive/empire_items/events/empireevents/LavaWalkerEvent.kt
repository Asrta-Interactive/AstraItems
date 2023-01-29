package com.astrainteractive.empire_items.events.empireevents

import com.astrainteractive.empire_items.di.blockPlacerModule
import com.astrainteractive.empire_items.models.bukkit.EmpireEnchants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astralibs.events.DSLEvent
import ru.astrainteractive.astralibs.utils.persistence.Persistence.hasPersistentData

class LavaWalkerEvent{
    private val blockPlacer by blockPlacerModule

    fun Block.setTypeFast(type: Material) =
        PluginScope.launch(Dispatchers.IO) { blockPlacer.setTypeFast(type, emptyMap(),null,this@setTypeFast) }

    private val limitedIO = Dispatchers.IO.limitedParallelism(1)
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

    val playerFireEvent = DSLEvent.event<EntityDamageEvent>  { e ->
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


    val playerMoveEvent = DSLEvent.event<PlayerMoveEvent>  { e ->
        val itemStack = e.player.inventory.boots ?: return@event
        val itemMeta = itemStack.itemMeta ?: return@event
        PluginScope.launch(limitedIO) {

            if (!hasLaveWalker(itemMeta)) return@launch
            val onToBlock = e.to.block.getRelative(BlockFace.DOWN)

            if (onToBlock.type == Material.LAVA)
                createBlocks(onToBlock)
        }

    }

}
