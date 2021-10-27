package com.makeevrserg.empireprojekt.empirelibs.menu

import com.makeevrserg.empireprojekt.empirelibs.callSyncMethod
import com.makeevrserg.empireprojekt.empirelibs.runAsyncTask
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder


/**
 * PlayerMenuUtility data class
 *
 * Don't use just Player class
 */
data class PlayerMenuUtility(val player: Player) {
    val previousItems: MutableList<String> = mutableListOf()
    var categoryId: String? = null
}

/**
 * Default menu abstract class
 */
public abstract class Menu(open var playerMenuUtility: PlayerMenuUtility) : InventoryHolder {

    private lateinit var inventory: Inventory

    /**
     * Title of this inventory
     */
    abstract var menuName: String

    /**
     * Size of inventory
     *
     * Shoul be in [9;54] and divided by 9
     */
    abstract val menuSize: Int

    /**
     * Menu handler
     */
    abstract fun handleMenu(e: InventoryClickEvent)

    /**
     * Function for setting items in menu
     */
    abstract fun setMenuItems()

    /**
     * Open inventory method for Menu class
     */
    fun open() {
        runAsyncTask {
            inventory = Bukkit.createInventory(this, menuSize, menuName)
            setMenuItems()
            callSyncMethod {
                playerMenuUtility.player.openInventory(inventory)
            }
        }
    }

    override fun getInventory() = inventory

}