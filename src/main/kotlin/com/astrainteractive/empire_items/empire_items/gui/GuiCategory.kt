package com.astrainteractive.empire_items.empire_items.gui

import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.events.EventManager
import ru.astrainteractive.astralibs.menu.AstraMenuSize
import ru.astrainteractive.astralibs.utils.convertHex
import com.astrainteractive.empire_items.api.EmpireItemsAPI.toAstraItemOrItem
import com.astrainteractive.empire_items.api.models.GUI_CONFIG
import com.astrainteractive.empire_items.api.utils.emoji
import com.astrainteractive.empire_items.empire_items.gui.crafting.GuiCrafting
import kotlinx.coroutines.launch
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.menu.PaginatedMenu

class GuiCategory(override val playerMenuUtility: PlayerMenuUtility) : PaginatedMenu() {

    val category = GUI_CONFIG.categories[playerMenuUtility.categoryId]!!

    override var menuTitle: String = convertHex(category.title).emoji()

    override val menuSize: AstraMenuSize = AstraMenuSize.XL
    override val backPageButton = GUI_CONFIG.settings.buttons.backButton.toAstraItemOrItem()!!.toInventoryButton(49)
    override val maxItemsAmount: Int = category.items.size
    override val nextPageButton = GUI_CONFIG.settings.buttons.nextButton.toAstraItemOrItem()!!.toInventoryButton(53)
    override var page: Int = playerMenuUtility.categoryPage

    override val prevPageButton = GUI_CONFIG.settings.buttons.prevButton.toAstraItemOrItem()!!.toInventoryButton(45)


    override fun loadPage(next: Int) {
        super.loadPage(next)
        playerMenuUtility.categoryPage += next
    }

    override fun onInventoryClicked(e: InventoryClickEvent) {
        super.onInventoryClicked(e)
        when (e.slot) {
            backPageButton.index -> {
                PluginScope.launch {
                    GuiCategories(playerMenuUtility.player).open()
                }
            }

            prevPageButton.index, nextPageButton.index -> {

            }

            else -> {
                PluginScope.launch {
                    playerMenuUtility.prevItems.add(category.items[getIndex(e.slot)])
                    GuiCrafting(playerMenuUtility).open()
                }
            }
        }
    }

    override fun onCreated() {
        setMenuItems()
    }

    override fun onInventoryClose(it: InventoryCloseEvent) {}
    override fun onPageChanged() {
        setMenuItems()
    }

    fun setMenuItems() {
        setManageButtons()
        val items = GUI_CONFIG.categories.values ?: return
        for (i in 0 until maxItemsPerPage) {
            val index = getIndex(i)
            inventory.setItem(i, category.items.getOrNull(index)?.toAstraItemOrItem() ?: continue)
        }

    }

}