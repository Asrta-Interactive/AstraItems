package com.makeevrserg.empireprojekt.menumanager.menu

import com.makeevrserg.empireprojekt.EmpirePlugin.Companion.plugin
import com.makeevrserg.empireprojekt.menumanager.PaginatedMenu
import com.makeevrserg.empireprojekt.menumanager.PlayerMenuUtility
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import com.makeevrserg.empireprojekt.util.EmpireUtils
import org.bukkit.Sound

class EmpireCategoriesMenu(playerMenuUtility: PlayerMenuUtility?) :
    PaginatedMenu(plugin.translations, playerMenuUtility) {
    private val guiConfigFile = plugin.empireFiles.guiFile.getConfig()
    override var menuName: String = EmpireUtils.HEXPattern(
        guiConfigFile?.getString("settings.categories_text", "Категории")!!
    )

    override val slots: Int
        get() {
            return 54
        }


    override fun handleMenu(e: InventoryClickEvent) {

        e.currentItem ?: return


        if ((e.slot != 45) && (e.slot != 49) && (e.slot != 53))
            EmpireCategoryMenu(
                playerMenuUtility,
                e.slot,
                page
            ).open()

        if (e.slot == 45)
            if (checkFirstPage()) return
            else reloadPage(-1)
        else if (e.slot == 49)
            e.whoClicked.closeInventory()
        else if (e.slot == 53)
            if (checkLastPage()) return
            else reloadPage(1)


    }


    private fun _getMaxPages(): Int {
        val size: Int = plugin.categoryItems.size
        var mP: Int = size / maxItemsPerPage
        mP += if ((size % maxItemsPerPage > 0)) 1 else 0
        return mP - 1
    }


    override fun setMenuItems() {
        addManageButtons()
        for (i in 0 until super.maxItemsPerPage) {
            index = super.maxItemsPerPage * page + i
            if (index < plugin.categoryItems.size) {
                val categoryItem: CategoryItems.CategorySection = plugin.categoryItems.values.elementAt(index)
                val menuItem: String = categoryItem.icon
                val itemStack: ItemStack = plugin.empireItems.empireItems[menuItem]?.clone() ?: (ItemStack(
                    Material.getMaterial(menuItem) ?: Material.PAPER
                ))
                val itemMeta: ItemMeta = itemStack.itemMeta
                itemMeta.setDisplayName(EmpireUtils.HEXPattern(categoryItem.name))
                itemMeta.lore = EmpireUtils.HEXPattern(categoryItem.lore)
                itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
                itemStack.itemMeta = itemMeta
                inventory.setItem(i, itemStack)
            }
        }
    }

    private fun playInventorySound() {

        playerMenuUtility.player.playSound(
            playerMenuUtility.player.location,
            guiConfigFile?.getString("settings.categories_sound") ?: Sound.ITEM_BOOK_PAGE_TURN.name.toLowerCase(),
            1.0f,
            1.0f
        )

    }

    init {
        maxPages = _getMaxPages()
        playInventorySound()
    }
}
