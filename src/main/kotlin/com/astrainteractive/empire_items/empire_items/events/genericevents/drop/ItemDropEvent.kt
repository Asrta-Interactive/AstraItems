package com.astrainteractive.empire_items.empire_items.events.genericevents.drop

import com.astrainteractive.astralibs.events.DSLEvent
import com.astrainteractive.astralibs.events.EventListener
import com.astrainteractive.empire_items.api.EmpireItemsAPI
import com.astrainteractive.empire_items.api.items.BlockParser
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
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.loot.Lootable

class ItemDropEvent {

    private val blockLocations: MutableList<Location> = mutableListOf()


    val onFishingEvent = DSLEvent.event(PlayerFishEvent::class.java)  { e ->
        val caught = e.caught ?: return@event
        EmpireItemsAPI.dropByDropFrom["PlayerFishEvent"]?.forEach {
            it.performDrop(caught.location)
        }
    }

    val onBlockBreak = DSLEvent.event(BlockBreakEvent::class.java)  { e ->
        val block: Block = e.block
        val customBlockData = BlockParser.getBlockData(e.block)
        val customBlock = EmpireItemsAPI.itemYamlFilesByID.values.firstOrNull { it.block?.data==customBlockData }
        val customBlockId = customBlock?.id
        if (customBlock?.block?.ignoreCheck != true) {
            if (blockLocations.contains(block.location))
                return@event
            else
                blockLocations.add(block.location)
            if (blockLocations.size > 60)
                blockLocations.removeAt(0)
        }

        val dropFrom = customBlockId ?: block.blockData.material.name
        EmpireItemsAPI.dropByDropFrom[dropFrom]?.forEach {
            it.performDrop(block.location)
        }?.let {
            e.isDropItems = false
        }
    }


    val inventoryOpenEvent = DSLEvent.event(PlayerInteractEvent::class.java)  { e ->
        if (e.action != Action.RIGHT_CLICK_BLOCK)
            return@event
        val block = e.clickedBlock ?: return@event
        if (block.state !is Chest)
            return@event
        val chest = block.state as Chest
        val lootable = chest as Lootable
        lootable.lootTable ?: return@event
        EmpireItemsAPI.dropByDropFrom["PlayerInteractEvent"]?.forEach {
            it.generateItem()?.let { chest.blockInventory.addItem(it) }
        }
    }

    val onMobDeath = DSLEvent.event(EntityDeathEvent::class.java)  { e ->
        val dropFrom = MobApi.getActiveEntity(e.entity)?.empireMob?.id ?: e.entity.type.name
        MobApi.removeActiveEntity(e.entity)
        EmpireItemsAPI.dropByDropFrom[dropFrom]?.forEach {
            it.performDrop(e.entity.location)
        }
    }
}