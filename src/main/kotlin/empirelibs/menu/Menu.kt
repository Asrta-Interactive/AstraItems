package empirelibs.menu

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder


data class PlayerMenuUtility(val player: Player){
    val previousItems:MutableList<String> = mutableListOf()
}

public abstract class Menu(open var playerMenuUtility: PlayerMenuUtility) :InventoryHolder {

    private lateinit var inventory: Inventory
    //Title of inventory
    abstract var menuName: String
    //Size of inventory. Must be [9;54] and divided by 9
    abstract val menuSize: Int
    abstract fun handleMenu(e: InventoryClickEvent)
    abstract fun setMenuItems()

    fun open() {
        inventory = Bukkit.createInventory(this, menuSize, menuName)
        setMenuItems()
        playerMenuUtility.player.openInventory(inventory)
    }

    override fun getInventory() = inventory

}
