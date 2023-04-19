package com.astrainteractive.empire_items.util

import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.menu.IInventoryButton

fun ItemStack.toInventoryButton(index: Int) = object : IInventoryButton {
    override val index: Int = index
    override val item: ItemStack = this@toInventoryButton
    override val onClick: (e: InventoryClickEvent) -> Unit ={}
}