package com.astrainteractive.empire_items.empire_items.gui

import com.astrainteractive.astralibs.async.AsyncHelper
import com.astrainteractive.astralibs.events.EventManager
import com.astrainteractive.astralibs.menu.AstraMenuSize
import com.astrainteractive.astralibs.utils.convertHex
import com.astrainteractive.empire_items.api.EmpireItemsAPI.toAstraItemOrItem
import com.astrainteractive.empire_items.api.models.GUI_CONFIG
import com.astrainteractive.empire_items.api.utils.emoji
import com.astrainteractive.empire_items.empire_items.gui.crafting.GuiCrafting
import kotlinx.coroutines.launch
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack

class GuiCategory(override val playerMenuUtility: PlayerMenuUtility) :
    AstraPaginatedMenu() {

    val category = GUI_CONFIG.categories[playerMenuUtility.categoryId]!!

    override var menuName: String = convertHex(category.title).emoji()

    override val menuSize: AstraMenuSize = AstraMenuSize.XL
    override val backPageButton: ItemStack = GUI_CONFIG.settings.buttons.backButton.toAstraItemOrItem()!!
    override val maxItemsAmount: Int = category.items.size
    override val nextPageButton: ItemStack = GUI_CONFIG.settings.buttons.nextButton.toAstraItemOrItem()!!
    override var page: Int = playerMenuUtility.categoryPage
    override val prevButtonIndex: Int = 45
    override val backButtonIndex: Int = 49
    override val nextButtonIndex: Int = 53

    override val prevPageButton: ItemStack = GUI_CONFIG.settings.buttons.prevButton.toAstraItemOrItem()!!


    override fun loadPage(next:Int){
        super.loadPage(next)
        playerMenuUtility.categoryPage+=next
    }

    override fun onDestroy(it: InventoryCloseEvent, manager: EventManager) {
    }

    override fun handleMenu(e: InventoryClickEvent) {
        super.handleMenu(e)
        when(e.slot){
            backButtonIndex->{
                AsyncHelper.launch{
                    GuiCategories(playerMenuUtility.player).open()
                }
            }
            prevButtonIndex,nextButtonIndex->{

            }
            else->{
                AsyncHelper.launch{
                    playerMenuUtility.prevItems.add(category.items[getIndex(e.slot)])
                    GuiCrafting(playerMenuUtility).open()
                }
            }
        }
    }
    override fun setMenuItems() {
        addManageButtons()
        val items = GUI_CONFIG.categories.values ?: return
        for (i in 0 until maxItemsPerPage) {
            val index = getIndex(i)
            inventory.setItem(i, category.items.getOrNull(index)?.toAstraItemOrItem()?:continue)
        }

    }

}