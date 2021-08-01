package com.makeevrserg.empireprojekt.menumanager.emgui

import com.makeevrserg.empireprojekt.EmpirePlugin
import empirelibs.menu.PaginatedMenu
import empirelibs.menu.PlayerMenuUtility
import empirelibs.EmpireUtils
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack


class EmpireCategoryMenu(
    override var playerMenuUtility: PlayerMenuUtility,
    val slot: Int,
    override var page: Int
) : PaginatedMenu(playerMenuUtility) {
    override val menuSize = 54
    override var maxItemsPerPage: Int = 45
    override var slotsAmount: Int = EmpirePlugin.instance.guiCategories.categoriesMap.values.elementAt(slot).items.size
    override var maxPages: Int = getMaxPages()
    private fun playInventorySound() {
        playerMenuUtility.player.playSound(
            playerMenuUtility.player.location,
            EmpirePlugin.instance.guiSettings.categorySound,
            1.0f,
            1.0f
        )
    }

    init {
        playInventorySound()
    }


    override var menuName: String =
        EmpireUtils.HEXPattern(EmpirePlugin.instance.guiCategories.categoriesMap.values.elementAt(slot).title)


    override fun handleMenu(e: InventoryClickEvent) {
        e ?: return
        e.currentItem ?: return
        when (e.slot) {
            getBackButtonIndex() -> {
                e.whoClicked.closeInventory()
                EmpireCategoriesMenu(playerMenuUtility).open()
            }
            getNextButtonIndex() -> {
                if (isLastPage())
                    return
                loadPage(1)
            }
            getPrevButtonIndex() -> {
                if (isFirstPage())
                    return
                loadPage(-1)
            }
            else -> {
                playerMenuUtility.previousItems.clear()
                EmpireCraftMenu(
                    playerMenuUtility,
                    slot,
                    page,
                    EmpirePlugin.instance.guiCategories.categoriesMap.values.elementAt(slot).items[page * maxItemsPerPage + e.slot],
                    0
                ).open()
            }
        }

    }


    override fun setMenuItems() {
        addManageButtons()
        for (i in 0 until maxItemsPerPage) {
            val index = getIndex(i)
            if (index >= EmpirePlugin.instance.guiCategories.categoriesMap.values.elementAt(slot).items.size)
                return

            val menuItem: String = EmpirePlugin.instance.guiCategories.categoriesMap.values.elementAt(slot).items[index]

            val itemStack: ItemStack = EmpirePlugin.empireItems.empireItems[menuItem]?.clone() ?: (ItemStack(
                Material.getMaterial(menuItem) ?: Material.PAPER
            ))

            inventory.setItem(i, itemStack.clone())
        }
    }
}
