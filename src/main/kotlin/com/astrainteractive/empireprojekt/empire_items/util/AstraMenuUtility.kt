package com.astrainteractive.empireprojekt.astralibs.menu

import com.astrainteractive.astralibs.callSyncMethod
import com.astrainteractive.astralibs.menu.AstraPlayerMenuUtility
import com.astrainteractive.astralibs.menu.Menu
import com.astrainteractive.astralibs.menu.PaginatedMenu
import com.astrainteractive.astralibs.runAsyncTask
import com.astrainteractive.empireprojekt.EmpirePlugin
import com.astrainteractive.empireprojekt.empire_items.api.ItemsAPI
import com.astrainteractive.empireprojekt.empire_items.api.ItemsAPI.asEmpireItem
import com.astrainteractive.empireprojekt.empire_items.util.setDisplayName
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack


/**
 * PlayerMenuUtility data class
 *
 * Don't use just Player class
 */
class PlayerMenuUtility(
    override var player: Player,
    val previousItems: MutableList<String> = mutableListOf(),
    var categoryId: String? = null
    ): AstraPlayerMenuUtility(player)

abstract class AbstractMenu(playerMenuUtility: PlayerMenuUtility):Menu(){
    abstract override val playerMenuUtility: PlayerMenuUtility
}
abstract class AbstractPaginatedMenu(playerMenuUtility: PlayerMenuUtility) : PaginatedMenu(){
    abstract override val playerMenuUtility: PlayerMenuUtility

    override val backPageButton: ItemStack = EmpirePlugin.instance.guiSettings.backButton.asEmpireItem()?: ItemStack(Material.PAPER).apply {
        setDisplayName(EmpirePlugin.translations.BACK_PAGE)
    }
    override val nextPageButton: ItemStack= EmpirePlugin.instance.guiSettings.nextButton.asEmpireItem()?: ItemStack(Material.PAPER).apply {
        setDisplayName(EmpirePlugin.translations.NEXT_PAGE)
    }
    override val prevPageButton: ItemStack= EmpirePlugin.instance.guiSettings.prevButton.asEmpireItem()?: ItemStack(Material.PAPER).apply {
        setDisplayName(EmpirePlugin.translations.BACK_PAGE)
    }
}