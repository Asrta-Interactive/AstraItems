package com.astrainteractive.empire_items.empire_items.events.genericevents.drop

import com.astrainteractive.empire_items.empire_items.api.drop.DropManager
import com.astrainteractive.astralibs.EventListener
import com.astrainteractive.empire_items.empire_items.api.drop.AstraDrop
import com.astrainteractive.empire_items.empire_items.api.items.BlockParser
import com.astrainteractive.empire_items.empire_items.api.items.data.ItemManager
import com.astrainteractive.empire_items.empire_items.api.items.data.ItemManager.getItemStack
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.block.Chest
import org.bukkit.entity.Entity
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.player.PlayerFishEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.loot.Lootable
import kotlin.random.Random

class ItemDropListener : EventListener {

    private val blockLocations: MutableList<Location> = mutableListOf()


    @EventHandler
    fun onFishingEvent(e: PlayerFishEvent) {
        val caught = e.caught ?: return
        DropManager.spawnDrop("PlayerFishEvent",caught.location)
    }

    @EventHandler
    fun onBlockBreak(e: BlockBreakEvent) {
        val block: Block = e.block
        val customBlockData = BlockParser.getBlockData(e.block)
        val customBlock = ItemManager.getBlockInfoByData(customBlockData)
        val customBlockId = customBlock?.id
        if (customBlock?.block?.ignoreCheck != true) {
            if (blockLocations.contains(block.location))
                return
            else
                blockLocations.add(block.location)
            if (blockLocations.size > 60)
                blockLocations.removeAt(0)
        }

        val dropFrom = customBlockId ?: block.blockData.material.name
        if (DropManager.spawnDrop(dropFrom,block.location))
            e.isDropItems = false
    }


    @EventHandler
    fun inventoryOpenEvent(e: PlayerInteractEvent) {
        if (e.action != Action.RIGHT_CLICK_BLOCK)
            return
        val block = e.clickedBlock ?: return
        if (block.state !is Chest)
            return
        val chest = block.state as Chest
        val lootable = chest as Lootable
        lootable.lootTable ?: return
        val drops = DropManager.getDropsFrom("PlayerInteractEvent")
        DropManager.getDrops(drops).forEach {
            chest.blockInventory.addItem(it)
        }
    }

    @EventHandler
    fun onMobDeath(e: EntityDeathEvent) {
        val entity: Entity = e.entity
        DropManager.spawnDrop(entity.type.name,entity.location)
    }

    override fun onDisable() {
        EntityDeathEvent.getHandlerList().unregister(this)
        BlockBreakEvent.getHandlerList().unregister(this)
        PlayerFishEvent.getHandlerList().unregister(this)
        PlayerInteractEvent.getHandlerList().unregister(this)

    }
}