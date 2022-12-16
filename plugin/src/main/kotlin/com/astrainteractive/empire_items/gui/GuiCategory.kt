package com.astrainteractive.empire_items.gui

import com.astrainteractive.empire_items.di.GuiConfigModule
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.menu.AstraMenuSize
import ru.astrainteractive.astralibs.utils.convertHex
import com.astrainteractive.empire_itemss.api.utils.emoji
import com.astrainteractive.empire_items.gui.crafting.GuiCrafting
import com.astrainteractive.empire_items.util.EmpireItemsAPIExt.toAstraItemOrItem
import com.atrainteractive.empire_items.models.config.GuiConfig
import kotlinx.coroutines.launch
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astralibs.menu.PaginatedMenu

class GuiCategory(override val playerMenuUtility: PlayerMenuUtility, private val guiConfig: GuiConfig) : PaginatedMenu() {

    val category = guiConfig.categories[playerMenuUtility.categoryId]!!

    override var menuTitle: String = convertHex(category.title).emoji()

    override val menuSize: AstraMenuSize = AstraMenuSize.XL
    override val backPageButton = guiConfig.settings.buttons.backButton.toAstraItemOrItem()!!.toInventoryButton(49)
    override val maxItemsAmount: Int = category.items.size
    override val nextPageButton = guiConfig.settings.buttons.nextButton.toAstraItemOrItem()!!.toInventoryButton(53)
    override var page: Int = playerMenuUtility.categoryPage

    override val prevPageButton = guiConfig.settings.buttons.prevButton.toAstraItemOrItem()!!.toInventoryButton(45)


    override fun loadPage(next: Int) {
        super.loadPage(next)
        playerMenuUtility.categoryPage += next
    }

    override fun onInventoryClicked(e: InventoryClickEvent) {
        super.onInventoryClicked(e)
        e.isCancelled = true
        when (e.slot) {
            backPageButton.index -> {
                PluginScope.launch {
                    GuiCategories(playerMenuUtility.player, guiConfig = GuiConfigModule.value).open()
                }
            }

            prevPageButton.index, nextPageButton.index -> {

            }

            else -> {
                PluginScope.launch {
                    playerMenuUtility.prevItems.add(category.items[getIndex(e.slot)])
                    GuiCrafting(playerMenuUtility, GuiConfigModule.value).open()
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
        val items = guiConfig.categories.values ?: return
        for (i in 0 until maxItemsPerPage) {
            val index = getIndex(i)
            inventory.setItem(i, category.items.getOrNull(index)?.toAstraItemOrItem() ?: continue)
        }

    }

}