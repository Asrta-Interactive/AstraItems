package com.astrainteractive.empire_items.empire_items.events.genericevents.drop

import com.astrainteractive.empire_items.api.drop.DropApi
import com.astrainteractive.astralibs.EventListener
import com.astrainteractive.empire_items.api.items.BlockParser
import com.astrainteractive.empire_items.api.items.data.ItemApi
import com.astrainteractive.empire_items.api.mobs.MobApi
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.block.Chest
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.player.PlayerFishEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.loot.Lootable

class ItemDropEvent : EventListener {

    private val blockLocations: MutableList<Location> = mutableListOf()


    @EventHandler
    fun onFishingEvent(e: PlayerFishEvent) {
        val caught = e.caught ?: return
        DropApi.spawnDrop("PlayerFishEvent", caught.location)
    }

    @EventHandler
    fun onBlockBreak(e: BlockBreakEvent) {
        val block: Block = e.block
        val customBlockData = BlockParser.getBlockData(e.block)
        val customBlock = ItemApi.getBlockInfoByData(customBlockData)
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
        if (DropApi.spawnDrop(dropFrom, block.location))
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
        val drops = DropApi.getDropsFrom("PlayerInteractEvent")
        DropApi.getDrops(drops).forEach {
            chest.blockInventory.addItem(it)
        }
    }

    @EventHandler
    fun onMobDeath(e: EntityDeathEvent) {
        val dropFrom = MobApi.getActiveEntity(e.entity)?.empireMob?.id ?: e.entity.type.name
        MobApi.removeActiveEntity(e.entity)
        DropApi.spawnDrop(dropFrom, e.entity.location)
    }

    override fun onDisable() {
        EntityDeathEvent.getHandlerList().unregister(this)
        BlockBreakEvent.getHandlerList().unregister(this)
        PlayerFishEvent.getHandlerList().unregister(this)
        PlayerInteractEvent.getHandlerList().unregister(this)

    }
}