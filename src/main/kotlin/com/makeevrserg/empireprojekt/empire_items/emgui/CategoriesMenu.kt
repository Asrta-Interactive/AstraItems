package com.makeevrserg.empireprojekt.empire_items.emgui

import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.empirelibs.menu.PaginatedMenu
import com.makeevrserg.empireprojekt.empirelibs.menu.PlayerMenuUtility
import com.makeevrserg.empireprojekt.empirelibs.EmpireUtils
import com.makeevrserg.empireprojekt.empirelibs.asEmpireItem
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.meta.ItemMeta

class EmpireCategoriesMenu(playerMenuUtility: PlayerMenuUtility?) :
    PaginatedMenu(playerMenuUtility) {
    override var menuName: String = EmpireUtils.HEXPattern(EmpirePlugin.instance.guiSettings.categoriesText)
    override var page: Int = 0
    override var maxItemsPerPage: Int = 45
    override val menuSize = 54
    override var slotsAmount: Int = EmpirePlugin.instance.guiCategories.keys.size
    override var maxPages = getMaxPages()

    override fun handleMenu(e: InventoryClickEvent) {
        e.currentItem ?: return
        if ((e.slot != 45) && (e.slot != 49) && (e.slot != 53))
            EmpireCategoryMenu(
                playerMenuUtility,
                e.slot,
                page
            ).open()

        if (e.slot == getPrevButtonIndex())
            if (isFirstPage()) return
            else loadPage(-1)
        else if (e.slot == getBackButtonIndex())
            e.whoClicked.closeInventory()
        else if (e.slot == getNextButtonIndex())
            if (isLastPage()) return
            else loadPage(1)


    }


    override fun setMenuItems() {
        addManageButtons()
        for (i in 0 until maxItemsPerPage) {
            val index = maxItemsPerPage * page + i
            if (index < EmpirePlugin.instance.guiCategories.keys.size) {
                val categoryName = EmpirePlugin.instance.guiCategories.keys.elementAt(index)
                val categoryItem = EmpirePlugin.instance.guiCategories[categoryName]!!
                val itemStack = categoryItem.icon.asEmpireItem()?.clone()?:continue
                val itemMeta: ItemMeta = itemStack.itemMeta?:continue
                itemMeta.setDisplayName(EmpireUtils.HEXPattern(categoryItem.name))
                itemMeta.lore = EmpireUtils.HEXPattern(categoryItem.lore)
                itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
                itemStack.itemMeta = itemMeta
                inventory.setItem(i, itemStack.clone())
            }
        }
    }

    private fun playInventorySound() {

        playerMenuUtility.player.playSound(
            playerMenuUtility.player.location,
            EmpirePlugin.instance.guiSettings.categoriesSound,
            1.0f,
            1.0f
        )

    }

    init {
        playInventorySound()
    }
}
