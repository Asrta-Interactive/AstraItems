package com.astrainteractive.empire_items.gui

import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.menu.AstraMenuSize
import ru.astrainteractive.astralibs.utils.convertHex
import com.astrainteractive.empire_itemss.api.EmpireItemsAPI.toAstraItemOrItem
import com.astrainteractive.empire_itemss.api.utils.setDisplayName
import com.astrainteractive.empire_itemss.api.utils.emoji
import com.atrainteractive.empire_items.models.config.GuiConfig
import kotlinx.coroutines.launch
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import ru.astrainteractive.astralibs.menu.PaginatedMenu


class GuiCategories(player: Player, override val playerMenuUtility: PlayerMenuUtility = PlayerMenuUtility(player),private val guiConfig: GuiConfig) :
    PaginatedMenu() {

    override var menuTitle: String = convertHex(guiConfig.settings.titles.categoriesText).emoji()

    override val menuSize: AstraMenuSize = AstraMenuSize.XL
    override val backPageButton = guiConfig.settings.buttons.backButton.toAstraItemOrItem()!!.toInventoryButton(49)
    override val maxItemsAmount: Int = guiConfig.categories.size ?: 0
    override val nextPageButton = guiConfig.settings.buttons.nextButton.toAstraItemOrItem()!!.toInventoryButton(53)
    override var page: Int = 0
    override val prevPageButton = guiConfig.settings.buttons.prevButton.toAstraItemOrItem()!!.toInventoryButton(45)

    override fun onInventoryClicked(e: InventoryClickEvent) {
        super.onInventoryClicked(e)
        e.isCancelled = true
        when (e.slot) {
            backPageButton.index -> {
                playerMenuUtility.player.closeInventory()
            }

            else -> {
                PluginScope.launch {
                    playerMenuUtility.categoriesPage = page
                    playerMenuUtility.categoryPage = 0
                    playerMenuUtility.categoryId =
                        guiConfig.categories.values.elementAt(getIndex(e.slot)).id ?: return@launch
                    GuiCategory(playerMenuUtility,guiConfig).open()
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
        val items = guiConfig.categories.values ?: return
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