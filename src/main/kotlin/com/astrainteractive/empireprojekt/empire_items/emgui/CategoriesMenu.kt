package com.astrainteractive.empireprojekt.empire_items.emgui

import com.astrainteractive.astralibs.AstraUtils
import com.astrainteractive.astralibs.menu.AstraPlayerMenuUtility
import com.astrainteractive.empireprojekt.EmpirePlugin
import com.astrainteractive.empireprojekt.empire_items.api.ItemsAPI.asEmpireItem
import com.astrainteractive.empireprojekt.astralibs.menu.AbstractPaginatedMenu
import com.astrainteractive.empireprojekt.astralibs.menu.PlayerMenuUtility
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

class EmpireCategoriesMenu(override val playerMenuUtility: PlayerMenuUtility):
    AbstractPaginatedMenu(playerMenuUtility) {
    /**
     * Название
     */
    override var menuName: String = AstraUtils.HEXPattern(EmpirePlugin.instance.guiSettings.categoriesText)

    /**
     * Текущая страница
     */
    override var page: Int = 0

    /**
     * Максимальное количество предметов на странице
     */
    override var maxItemsPerPage: Int = 45

    /**
     * Размер страницы
     */
    override val menuSize = 54

    /**
     * Максимальное количество предметов, которое хотим зупихнуть в текущее меню
     */
    override var slotsAmount: Int = EmpirePlugin.instance.guiCategories.keys.size

    /**
     * Максимальное количество страниц
     */
    override var maxPages = getMaxPages()

    override fun handleMenu(e: InventoryClickEvent) {
        super.handleMenu(e)
        e.currentItem ?: return
        if ((e.slot != 45) && (e.slot != 49) && (e.slot != 53))
            EmpireCategoryMenu(
                playerMenuUtility,
                e.slot,
                page
            ).open()
        else if (e.slot == getBackButtonIndex())
            e.whoClicked.closeInventory()
    }


    override fun setMenuItems() {
        addManageButtons()
        for (i in 0 until maxItemsPerPage) {
            val index = maxItemsPerPage * page + i
            if (index >= EmpirePlugin.instance.guiCategories.keys.size)
                return

            val categoryName = EmpirePlugin.instance.guiCategories.keys.elementAt(index)
            val categoryItem = EmpirePlugin.instance.guiCategories[categoryName]!!
            val itemStack = categoryItem.icon.asEmpireItem()?.clone() ?: continue
            val itemMeta: ItemMeta = itemStack.itemMeta ?: continue
            itemMeta.setDisplayName(AstraUtils.HEXPattern(categoryItem.name))
            itemMeta.lore = AstraUtils.HEXPattern(categoryItem.lore)
            itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
            itemStack.itemMeta = itemMeta
            inventory.setItem(i, itemStack.clone())
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