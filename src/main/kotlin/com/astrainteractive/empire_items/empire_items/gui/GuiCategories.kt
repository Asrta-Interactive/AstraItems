package com.astrainteractive.empire_items.empire_items.gui

import com.astrainteractive.astralibs.async.AsyncHelper
import com.astrainteractive.astralibs.events.EventManager
import com.astrainteractive.astralibs.menu.AstraMenuSize
import com.astrainteractive.astralibs.utils.convertHex
import com.astrainteractive.empire_items.api.EmpireItemsAPI.toAstraItemOrItem
import com.astrainteractive.empire_items.api.utils.setDisplayName
import com.astrainteractive.empire_items.empire_items.util.emoji
import com.astrainteractive.empire_items.api.models.GUI_CONFIG
import kotlinx.coroutines.launch
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack

class GuiCategories(player: Player, override val playerMenuUtility: PlayerMenuUtility = PlayerMenuUtility(player)) :
    AstraPaginatedMenu() {

    override var menuName: String = convertHex(GUI_CONFIG.settings.titles.categoriesText).emoji()

    override val menuSize: AstraMenuSize = AstraMenuSize.XL
    override val backPageButton: ItemStack = GUI_CONFIG.settings.buttons.backButton.toAstraItemOrItem()!!
    override val maxItemsAmount: Int = GUI_CONFIG.categories.size ?: 0
    override val nextPageButton: ItemStack = GUI_CONFIG.settings.buttons.nextButton.toAstraItemOrItem()!!
    override var page: Int = 0
    override val prevPageButton: ItemStack = GUI_CONFIG.settings.buttons.prevButton.toAstraItemOrItem()!!
    override val prevButtonIndex: Int = 45
    override val backButtonIndex: Int = 49
    override val nextButtonIndex: Int = 53

    override fun handleMenu(e: InventoryClickEvent) {
        super.handleMenu(e)
        when (e.slot) {
            backButtonIndex -> {
                playerMenuUtility.player.closeInventory()
            }
            else -> {
                AsyncHelper.launch {
                    playerMenuUtility.categoriesPage = page
                    playerMenuUtility.categoryPage = 0
                    playerMenuUtility.categoryId =
                        GUI_CONFIG.categories.values.elementAt(getIndex(e.slot)).id ?: return@launch
                    GuiCategory(playerMenuUtility).open()
                }
            }
        }
    }

    override fun onInventoryClose(it: InventoryCloseEvent, manager: EventManager) = Unit
    override fun loadPage(next: Int) {
        super.loadPage(next)
        playerMenuUtility.categoriesPage += next
    }

    override fun setMenuItems() {
        addManageButtons()
        val items = GUI_CONFIG.categories.values ?: return
        for (i in 0 until maxItemsPerPage) {
            val index = getIndex(i)
            val category = items.elementAtOrNull(index)?:continue
            val item = category.icon.toAstraItemOrItem()?.apply {
                this.setDisplayName(convertHex(category.name))
                this.lore = category.lore
            }?:continue
            inventory.setItem(i, item ?: continue)
        }

    }
}