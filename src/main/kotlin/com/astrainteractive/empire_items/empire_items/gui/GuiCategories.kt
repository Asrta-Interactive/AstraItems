package com.astrainteractive.empire_items.empire_items.gui

import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.events.EventManager
import ru.astrainteractive.astralibs.menu.AstraMenuSize
import ru.astrainteractive.astralibs.utils.convertHex
import com.astrainteractive.empire_items.api.EmpireItemsAPI.toAstraItemOrItem
import com.astrainteractive.empire_items.api.utils.setDisplayName
import com.astrainteractive.empire_items.api.models.GUI_CONFIG
import com.astrainteractive.empire_items.api.utils.emoji
import kotlinx.coroutines.launch
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.menu.IInventoryButton
import ru.astrainteractive.astralibs.menu.PaginatedMenu


class GuiCategories(player: Player, override val playerMenuUtility: PlayerMenuUtility = PlayerMenuUtility(player)) :
    PaginatedMenu() {

    override var menuTitle: String = convertHex(GUI_CONFIG.settings.titles.categoriesText).emoji()

    override val menuSize: AstraMenuSize = AstraMenuSize.XL
    override val backPageButton = GUI_CONFIG.settings.buttons.backButton.toAstraItemOrItem()!!.toInventoryButton(49)
    override val maxItemsAmount: Int = GUI_CONFIG.categories.size ?: 0
    override val nextPageButton = GUI_CONFIG.settings.buttons.nextButton.toAstraItemOrItem()!!.toInventoryButton(53)
    override var page: Int = 0
    override val prevPageButton = GUI_CONFIG.settings.buttons.prevButton.toAstraItemOrItem()!!.toInventoryButton(45)

    override fun onInventoryClicked(e: InventoryClickEvent) {
        super.onInventoryClicked(e)
        when (e.slot) {
            backPageButton.index -> {
                playerMenuUtility.player.closeInventory()
            }

            else -> {
                PluginScope.launch {
                    playerMenuUtility.categoriesPage = page
                    playerMenuUtility.categoryPage = 0
                    playerMenuUtility.categoryId =
                        GUI_CONFIG.categories.values.elementAt(getIndex(e.slot)).id ?: return@launch
                    GuiCategory(playerMenuUtility).open()
                }
            }
        }
    }

    override fun loadPage(next: Int) {
        super.loadPage(next)
        playerMenuUtility.categoriesPage += next
    }

    override fun onInventoryClose(it: InventoryCloseEvent) {}
    override fun onPageChanged() {
        setMenuItems()
    }

    override fun onCreated() {
        setMenuItems()
    }

    fun setMenuItems() {
        setManageButtons()
        val items = GUI_CONFIG.categories.values ?: return
        for (i in 0 until maxItemsPerPage) {
            val index = getIndex(i)
            val category = items.elementAtOrNull(index) ?: continue
            val item = category.icon.toAstraItemOrItem()?.apply {
                this.setDisplayName(convertHex(category.name))
                this.lore = category.lore
            } ?: continue
            inventory.setItem(i, item ?: continue)
        }

    }
}