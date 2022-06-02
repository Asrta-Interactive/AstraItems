package com.astrainteractive.empire_items.empire_items.gui

import com.astrainteractive.astralibs.async.AsyncHelper
import com.astrainteractive.empire_items.empire_items.gui.data.GuiConfig
import com.astrainteractive.astralibs.menu.AstraMenuSize
import kotlinx.coroutines.launch
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class GuiCategories(player: Player, override val playerMenuUtility: PlayerMenuUtility = PlayerMenuUtility(player)) :
    AstraPaginatedMenu() {

    val guiSettings = GuiConfig.getGuiConfig()

    override var menuName: String = guiSettings.settings.categoriesText

    override val menuSize: AstraMenuSize = AstraMenuSize.XL
    override val backPageButton: ItemStack = guiSettings.settings.backButton
    override val maxItemsAmount: Int = guiSettings.categories?.size ?: 0
    override val nextPageButton: ItemStack = guiSettings.settings.nextButton
    override var page: Int = 0
    override val prevPageButton: ItemStack = guiSettings.settings.prevButton
    override val prevButtonIndex: Int = 45
    override val backButtonIndex: Int = 49
    override val nextButtonIndex: Int = 53

    override fun handleMenu(e: InventoryClickEvent) {
        super.handleMenu(e)
        when(e.slot){
            backButtonIndex->{
                playerMenuUtility.player.closeInventory()
            }
            else->{
                AsyncHelper.launch{
                    playerMenuUtility.categoriesPage = page
                    playerMenuUtility.categoryPage = 0
                    playerMenuUtility.categoryId = guiSettings.categories?.values?.elementAt(getIndex(e.slot))?.id?:return@launch
                  GuiCategory(playerMenuUtility).open()
                }
            }
        }
    }
    override fun loadPage(next:Int){
        super.loadPage(next)
        playerMenuUtility.categoriesPage+=next
    }

    override fun setMenuItems() {
        addManageButtons()
        val items = guiSettings.categories?.values ?: return
        for (i in 0 until maxItemsPerPage) {
            val index = getIndex(i)
            inventory.setItem(i, items.elementAtOrNull(index)?.toItemStack()?:continue)
        }

    }
}