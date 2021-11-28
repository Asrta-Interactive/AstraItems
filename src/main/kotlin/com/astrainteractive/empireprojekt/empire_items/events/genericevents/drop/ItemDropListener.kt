package com.astrainteractive.empireprojekt.empire_items.events.genericevents.drop

import com.astrainteractive.empireprojekt.empire_items.api.drop.DropManager
import com.astrainteractive.astralibs.IAstraListener
import com.astrainteractive.empireprojekt.empire_items.api.drop.AstraDrop
import com.astrainteractive.empireprojekt.empire_items.api.items.BlockParser
import com.astrainteractive.empireprojekt.empire_items.api.items.data.ItemManager
import com.astrainteractive.empireprojekt.empire_items.api.items.data.ItemManager.getItemStack
import com.destroystokyo.paper.loottable.LootableInventory
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.block.Chest
import org.bukkit.entity.Entity
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.player.PlayerFishEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.loot.Lootable
import kotlin.random.Random

class ItemDropListener : IAstraListener {

    private val blockLocations: MutableList<Location> = mutableListOf()


    fun getDrops(list: List<AstraDrop>) = list.mapNotNull { drop->
        val chance = Random.nextDouble(0.0, 100.0)
        if (drop.percent < chance)
            return@mapNotNull null
        val amount = Random.nextInt(drop.minAmount, drop.maxAmount + 1)
        if (amount <= 0)
            return@mapNotNull null
        drop.id.getItemStack(amount)
    }

    private fun dropItem(list: List<AstraDrop>, l: Location): Boolean {
        var isDropped = false

        getDrops(list).forEach {
            isDropped = true
            l.world?.dropItem(l, it) ?: return isDropped
        }
        return isDropped
    }


    @EventHandler
    fun onFishingEvent(e: PlayerFishEvent) {
        val caught = e.caught ?: return
        val drops = DropManager.getDrops()["PlayerFishEvent"] ?: return
        dropItem(drops, caught.location)
    }

    @EventHandler
    fun onBlockBreak(e: BlockBreakEvent) {
        val block: Block = e.block
        if (blockLocations.contains(block.location))
            return
        else
            blockLocations.add(block.location)
        if (blockLocations.size > 60)
            blockLocations.removeAt(0)


        val customBlockData = BlockParser.getBlockData(e.block)
        val customBlockId = ItemManager.getBlockInfoByData(customBlockData)

        if (dropItem(
                DropManager.getDrops()[customBlockId ?: block.blockData.material.name] ?: listOf(),
                block.location
            )
        )
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
        lootable.lootTable?:return
        getDrops(DropManager.getDrops()["PlayerInteractEvent"] ?: return).forEach {
            chest.blockInventory.addItem(it)
        }
    }

    @EventHandler
    fun onMobDeath(e: EntityDeathEvent) {
        val entity: Entity = e.entity
        val listDrop = DropManager.getDrops()[entity.type.name] ?: return
        dropItem(listDrop, entity.location)
    }

    override fun onDisable() {
        EntityDeathEvent.getHandlerList().unregister(this)
        BlockBreakEvent.getHandlerList().unregister(this)
        PlayerFishEvent.getHandlerList().unregister(this)
        PlayerInteractEvent.getHandlerList().unregister(this)

    }
}