package com.makeevrserg.empireprojekt.emgui


import com.makeevrserg.empireprojekt.EmpirePlugin
import empirelibs.menu.PaginatedMenu
import empirelibs.menu.PlayerMenuUtility
import empirelibs.EmpireUtils
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack


class EmpireSoundsMenu(playerMenuUtility: PlayerMenuUtility?) :
    PaginatedMenu(playerMenuUtility) {
    override var menuName: String = EmpireUtils.HEXPattern(
        EmpirePlugin.empireFiles.guiFile.getConfig()?.getString("settings.sounds_text", "Звуки")!!
    )

    override var page: Int = 0
    override var slotsAmount: Int = EmpirePlugin.empireSounds.soundsList.size
    override var maxPages: Int = getMaxPages()
    override var menuSize: Int = 54
    override var maxItemsPerPage: Int = 45
    val sounds = EmpirePlugin.empireSounds.soundByID.keys



    override fun handleMenu(e: InventoryClickEvent) {
        e.currentItem ?: return

        if ((e.slot != 45) && (e.slot != 49) && (e.slot != 53)) {
            playerMenuUtility.player.playSound(playerMenuUtility.player.location,sounds.elementAt(maxItemsPerPage*page+e.slot),1.0f,1.0f)
        }

        if (e.slot == 45)
            if (isFirstPage()) return
            else loadPage(-1)
        else if (e.slot == 49)
            e.whoClicked.closeInventory()
        else if (e.slot == 53)
            if (isLastPage()) return
            else loadPage(1)


    }


//    private fun _getMaxPages(): Int {
//        val size: Int = EmpirePlugin.empireSounds.getSounds().keys.size
//        var mP: Int = size / maxItemsPerPage
//        mP += if ((size % maxItemsPerPage > 0)) 1 else 0
//        return mP - 1
//    }


    override fun setMenuItems() {
        addManageButtons()
        for (i in 0 until maxItemsPerPage) {
            val index = maxItemsPerPage * page + i
            if (index < sounds.size) {
                val itemStack = ItemStack(Material.PAPER)
                val itemMeta = itemStack.itemMeta!!
                itemMeta.setDisplayName(sounds.elementAt(index))
                itemStack.itemMeta = itemMeta
                inventory.setItem(i,itemStack)
            }
        }
    }


}
