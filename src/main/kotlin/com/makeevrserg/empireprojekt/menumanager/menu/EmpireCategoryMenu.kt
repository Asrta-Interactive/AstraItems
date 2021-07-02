package com.makeevrserg.empireprojekt.menumanager.menu

import com.makeevrserg.empireprojekt.menumanager.PaginatedMenu
import com.makeevrserg.empireprojekt.menumanager.PlayerMenuUtility
import com.makeevrserg.empireprojekt.util.EmpireUtils
import com.makeevrserg.empireprojekt.util.Translations.Companion.translations
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack


class EmpireCategoryMenu(
    override var playerMenuUtility: PlayerMenuUtility,
    val slot: Int,
    override var page: Int

) : PaginatedMenu(playerMenuUtility) {
    private var maxPage: Int

    val guiConfigFile = plugin.empireFiles.guiFile.getConfig()

    private fun playInventorySound() {


        playerMenuUtility.player.playSound(
            playerMenuUtility.player.location,
            guiConfigFile?.getString("settings.category_sound") ?: Sound.ITEM_BOOK_PAGE_TURN.name.toLowerCase(),
            1.0f,
            1.0f
        )

    }

    init {
        maxPages = _getMaxPages()
        maxPage = maxPages
        playInventorySound()
    }

    private fun _getMaxPages(): Int {
        val size: Int = plugin.categoryItems.values.elementAt(slot).items.size
        var mP = size / maxItemsPerPage
        mP += if (size % maxItemsPerPage > 0) 1 else 0
        return mP - 1
    }

    override var menuName: String = EmpireUtils.HEXPattern(plugin.categoryItems.values.elementAt(slot).title)

    override val slots: Int
        get() = 54


    override fun handleMenu(e: InventoryClickEvent) {
        e ?: return

        if (e.currentItem != null) {
            if (e.slot == 49) {
                e.whoClicked.closeInventory()
                EmpireCategoriesMenu(playerMenuUtility).open()
            } else if (e.slot == 53) {
                if (checkLastPage())
                    return
                reloadPage(1)
            } else if (e.slot == 45) {
                if (checkFirstPage())
                    return
                reloadPage(-1)
            } else {
                playerMenuUtility.previousItems.clear()
                EmpireCraftMenu(
                    playerMenuUtility,
                    slot,
                    page,
                    plugin.categoryItems.values.elementAt(slot).items[page * maxItemsPerPage + e.slot],
                    0
                ).open()
            }
        }
    }


    override fun setMenuItems() {
        addManageButtons()
        for (i in 0 until super.maxItemsPerPage) {
            index = super.maxItemsPerPage * page + i
            if (index >= plugin.categoryItems.values.elementAt(slot).items.size)
                return

            val menuItem: String = plugin.categoryItems.values.elementAt(slot).items[index]

            val itemStack: ItemStack = plugin.empireItems.empireItems[menuItem]?.clone() ?: (ItemStack(
                Material.getMaterial(menuItem) ?: Material.PAPER
            ))

            inventory.setItem(i, itemStack)
        }
    }
}
