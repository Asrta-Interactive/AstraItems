//package com.astrainteractive.empire_items.empire_items.events.upgrade
//
//import ru.astrainteractive.astralibs.events.DSLEvent
//import ru.astrainteractive.astralibs.events.EventListener
//import com.astrainteractive.empire_items.api.UpgradeApi
//import org.bukkit.event.EventHandler
//import org.bukkit.event.entity.EntityDeathEvent
//import org.bukkit.event.inventory.InventoryClickEvent
//import org.bukkit.event.inventory.PrepareAnvilEvent
//import org.bukkit.inventory.AnvilInventory
//import org.bukkit.inventory.InventoryView
//import org.bukkit.inventory.ItemStack
//
//class UpgradeEvent {
//
//    val onAnvilEvent = DSLEvent.event(PrepareAnvilEvent::class.java)  { e ->
//        val itemBefore: ItemStack = e.inventory.getItem(0) ?: return@event
//        val ingredient: ItemStack = e.inventory.getItem(1) ?: return@event
//
//        if (ingredient.amount > 1)
//            return@event
//        val itemAfter = itemBefore.clone()
//        var resultItem = UpgradeApi.addAttributes(itemAfter, ingredient) ?: return@event
//        resultItem = UpgradeApi.setUpgradeLore(resultItem)
//        e.result = resultItem
//        e.inventory.repairCost = 1
//
//    }
//
//
//    val inventoryClickEvent = DSLEvent.event(InventoryClickEvent::class.java)  { e ->
//
//        if (e.inventory !is AnvilInventory)
//            return@event
//        val view: InventoryView = e.view
//        val rawSlot = e.rawSlot
//        if (rawSlot != view.convertSlot(rawSlot))
//            return@event
//        if (rawSlot != 2)
//            return@event
//
//        val isUpgrade =
//            UpgradeApi.getAvailableUpgradesForItemStack((e.inventory as AnvilInventory).getItem(1) ?: return@event)
//                .isNotEmpty()
//        if (!isUpgrade)
//            return@event
//        UpgradeApi.setUpgradeLore((e.inventory as AnvilInventory).getItem(2) ?: return@event, false)
//    }
//}