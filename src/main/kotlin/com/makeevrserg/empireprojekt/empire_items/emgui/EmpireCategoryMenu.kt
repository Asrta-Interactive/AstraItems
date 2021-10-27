package com.makeevrserg.empireprojekt.empire_items.emgui

import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.empire_items.api.ItemsAPI
import com.makeevrserg.empireprojekt.empirelibs.menu.PaginatedMenu
import com.makeevrserg.empireprojekt.empirelibs.menu.PlayerMenuUtility
import com.makeevrserg.empireprojekt.empirelibs.EmpireUtils
import com.makeevrserg.empireprojekt.empirelibs.FileManager
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
    override var slotsAmount: Int = EmpirePlugin.instance.guiCategories.values.elementAt(slot).items.size
    override var maxPages: Int = getMaxPages()
    override var menuName: String =
        EmpireUtils.HEXPattern(EmpirePlugin.instance.guiCategories.values.elementAt(slot).title)

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
        playerMenuUtility.categoryId = EmpirePlugin.instance.guiCategories.keys.elementAt(slot)
    }


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
                    EmpirePlugin.instance.guiCategories.values.elementAt(slot).items[page * maxItemsPerPage + e.slot],
                    0
                ).open()
            }
        }

    }


    override fun setMenuItems() {
        addManageButtons()
        for (i in 0 until maxItemsPerPage) {
            val index = getIndex(i)
            if (index >= EmpirePlugin.instance.guiCategories.values.elementAt(slot).items.size)
                return

            val menuItem: String = EmpirePlugin.instance.guiCategories.values.elementAt(slot).items[index]

            val itemStack: ItemStack = ItemsAPI.getEmpireItemStackOrItemStack(menuItem)?.clone() ?: (ItemStack(
                Material.PAPER
            )).apply {
                val meta = itemMeta
                meta?.setDisplayName(menuItem) ?: return@apply
                itemMeta = meta
            }

            inventory.setItem(i, itemStack.clone())
        }
    }
}
