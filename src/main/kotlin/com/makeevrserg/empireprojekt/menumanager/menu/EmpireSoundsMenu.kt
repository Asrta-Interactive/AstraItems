package com.makeevrserg.empireprojekt.menumanager.menu


import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.menumanager.PaginatedMenu
import com.makeevrserg.empireprojekt.menumanager.PlayerMenuUtility
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import com.makeevrserg.empireprojekt.util.EmpireUtils

class EmpireSoundsMenu(playerMenuUtility: PlayerMenuUtility?) :
    PaginatedMenu(playerMenuUtility) {
    override var menuName: String = EmpireUtils.HEXPattern(
        EmpirePlugin.empireFiles.guiFile.getConfig()?.getString("settings.sounds_text", "Звуки")!!
    )

    val sounds = EmpirePlugin.empireSounds.getSounds().keys
    val namespace = EmpirePlugin.empireSounds.getNamespace()
    override val slots: Int
        get() {
            return 54
        }


    override fun handleMenu(e: InventoryClickEvent) {
        e.currentItem ?: return

        if ((e.slot != 45) && (e.slot != 49) && (e.slot != 53)) {
            playerMenuUtility.player.playSound(playerMenuUtility.player.location,namespace+":"+sounds.elementAt(super.maxItemsPerPage*page+e.slot),1.0f,1.0f)
        }

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
        val size: Int = EmpirePlugin.empireSounds.getSounds().keys.size
        var mP: Int = size / maxItemsPerPage
        mP += if ((size % maxItemsPerPage > 0)) 1 else 0
        return mP - 1
    }


    override fun setMenuItems() {
        addManageButtons()
        for (i in 0 until super.maxItemsPerPage) {
            index = super.maxItemsPerPage * page + i
            if (index < sounds.size) {
                val itemStack = ItemStack(Material.PAPER)
                val itemMeta = itemStack.itemMeta!!
                itemMeta.setDisplayName(sounds.elementAt(index))
                itemStack.itemMeta = itemMeta
                inventory.setItem(i,itemStack)
            }
        }
    }

    init {
        maxPages = _getMaxPages()
    }
}
