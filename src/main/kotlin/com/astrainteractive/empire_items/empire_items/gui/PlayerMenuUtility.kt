package com.astrainteractive.empire_items.empire_items.gui

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.menu.IInventoryButton
import ru.astrainteractive.astralibs.menu.IPlayerHolder


class PlayerMenuUtility(override var player: Player) : IPlayerHolder {

    var categoriesPage = 0
    var categoryId: String? = null
    var categoryPage = 0
    var craftingPage = 0
    val prevItems: MutableList<String> = mutableListOf()
}
fun ItemStack.toInventoryButton(index: Int) = object : IInventoryButton {
    override val index: Int = index
    override val item: ItemStack = this@toInventoryButton

}