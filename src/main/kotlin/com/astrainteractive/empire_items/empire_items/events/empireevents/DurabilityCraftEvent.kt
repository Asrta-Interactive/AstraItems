package com.astrainteractive.empire_items.empire_items.events.empireevents

import com.astrainteractive.astralibs.events.DSLEvent
import com.astrainteractive.astralibs.events.EventListener
import com.astrainteractive.empire_items.api.utils.BukkitConstants
import com.astrainteractive.empire_items.api.utils.getPersistentData
import com.astrainteractive.empire_items.api.utils.hasPersistentData
import com.astrainteractive.empire_items.api.utils.setPersistentDataType
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable

class DurabilityCraftEvent{

    fun ItemStack.manage(): ItemStack {
        val meta = itemMeta
        val craftDurability = meta?.getPersistentData(BukkitConstants.CRAFT_DURABILITY)!!-1
        val maxDurability = meta.getPersistentData(BukkitConstants.MAX_CUSTOM_DURABILITY)!!
        meta.setPersistentDataType(BukkitConstants.EMPIRE_DURABILITY,craftDurability)
        meta.setPersistentDataType(BukkitConstants.CRAFT_DURABILITY,craftDurability)
        if (craftDurability<=0)
            return this
        val d: Int = type.maxDurability -
                type.maxDurability * craftDurability / maxDurability
        amount+=1
        (meta as Damageable).damage = d
        itemMeta = meta
        return this
    }

    val entityResurrectEvent = DSLEvent.event(CraftItemEvent::class.java)  { e ->

        val a = e.inventory.matrix?.filter { it?.itemMeta?.hasPersistentData(BukkitConstants.CRAFT_DURABILITY)==true }
        if (a.isNullOrEmpty())
            return@event
        if (e.action!=InventoryAction.PICKUP_ALL) {
            e.isCancelled = true
            return@event
        }
        e.inventory.matrix?.toList()?.forEachIndexed { i, itemStack ->
            if (itemStack?.itemMeta?.hasPersistentData(BukkitConstants.CRAFT_DURABILITY)!=true)
                return@forEachIndexed
            e.inventory.setItem(i+1,itemStack.manage())
        }

    }
}