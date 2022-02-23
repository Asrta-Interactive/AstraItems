package com.astrainteractive.empire_items.empire_items.events.upgrade

import com.astrainteractive.astralibs.EventListener
import com.astrainteractive.empire_items.api.upgrade.UpgradeApi
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.inventory.AnvilInventory
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack

class UpgradeEvent : EventListener {

    @EventHandler
    fun onAnvilEvent(e: PrepareAnvilEvent) {
        val itemBefore: ItemStack = e.inventory.getItem(0) ?: return
        val ingredient: ItemStack = e.inventory.getItem(1) ?: return

        if (ingredient.amount > 1)
            return
        val itemAfter = itemBefore.clone()
        var resultItem = UpgradeApi.addAttributes(itemAfter, ingredient) ?: return
        resultItem = UpgradeApi.setUpgradeLore(resultItem)
        e.result = resultItem
        e.inventory.repairCost = 1

    }


    @EventHandler
    fun inventoryClickEvent(e: InventoryClickEvent) {

        if (e.inventory !is AnvilInventory)
            return
        val view: InventoryView = e.view
        val rawSlot = e.rawSlot
        if (rawSlot != view.convertSlot(rawSlot))
            return
        if (rawSlot != 2)
            return

        val isUpgrade =
            UpgradeApi.getAvailableUpgradesForItemStack((e.inventory as AnvilInventory).getItem(1) ?: return)
                .isNotEmpty()
        if (!isUpgrade)
            return
        UpgradeApi.setUpgradeLore((e.inventory as AnvilInventory).getItem(2) ?: return, false)
    }

    override fun onDisable() {
        PrepareAnvilEvent.getHandlerList().unregister(this)
        InventoryClickEvent.getHandlerList().unregister(this)
    }
}