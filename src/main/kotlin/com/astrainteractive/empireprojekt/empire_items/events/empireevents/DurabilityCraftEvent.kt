package com.astrainteractive.empireprojekt.empire_items.events.empireevents

import com.astrainteractive.astralibs.IAstraListener
import com.astrainteractive.empireprojekt.empire_items.api.utils.BukkitConstants
import com.astrainteractive.empireprojekt.empire_items.api.utils.getPersistentData
import com.astrainteractive.empireprojekt.empire_items.api.utils.hasPersistentData
import com.astrainteractive.empireprojekt.empire_items.api.utils.setPersistentDataType
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable

class DurabilityCraftEvent : IAstraListener {

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

    @EventHandler
    private fun entityResurrectEvent(e: CraftItemEvent) {

        val a = e.inventory.matrix.filter { it?.itemMeta?.hasPersistentData(BukkitConstants.CRAFT_DURABILITY)==true }
        if (a.isEmpty())
            return
        if (e.action!=InventoryAction.PICKUP_ALL) {
            e.isCancelled = true
            return
        }
        e.inventory.matrix.toList().forEachIndexed { i, itemStack ->
            if (itemStack?.itemMeta?.hasPersistentData(BukkitConstants.CRAFT_DURABILITY)!=true)
                return@forEachIndexed
            e.inventory.setItem(i+1,itemStack.manage())

        }

    }

    override fun onDisable() {
        CraftItemEvent.getHandlerList().unregister(this)
    }
}