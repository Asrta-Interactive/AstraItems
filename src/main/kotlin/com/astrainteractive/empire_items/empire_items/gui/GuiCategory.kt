package com.astrainteractive.empire_items.empire_items.gui

import com.astrainteractive.astralibs.async.AsyncHelper
import com.astrainteractive.empire_items.empire_items.gui.data.GuiConfig
import com.astrainteractive.astralibs.menu.AstraMenuSize
import com.astrainteractive.empire_items.api.EmpireItemsAPI.toAstraItemOrItem
import kotlinx.coroutines.launch
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class GuiCategory(override val playerMenuUtility: PlayerMenuUtility) :
    AstraPaginatedMenu() {

    val guiSettings = GuiConfig.getGuiConfig()
    val category = guiSettings.categories?.get(playerMenuUtility.categoryId)!!

    override var menuName: String = category.title

    override val menuSize: AstraMenuSize = AstraMenuSize.XL
    override val backPageButton: ItemStack = guiSettings.settings.backButton
    override val maxItemsAmount: Int = category.items.size
    override val nextPageButton: ItemStack = guiSettings.settings.nextButton
    override var page: Int = playerMenuUtility.categoryPage
    override val prevButtonIndex: Int = 45
    override val backButtonIndex: Int = 49
    override val nextButtonIndex: Int = 53

    override val prevPageButton: ItemStack = guiSettings.settings.prevButton


    override fun loadPage(next:Int){
        super.loadPage(next)
        playerMenuUtility.categoryPage+=next
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
        val items = guiSettings.categories?.values ?: return
        for (i in 0 until maxItemsPerPage) {
            val index = getIndex(i)
            inventory.setItem(i, category.items.getOrNull(index)?.toAstraItemOrItem()?:continue)
        }

    }
}