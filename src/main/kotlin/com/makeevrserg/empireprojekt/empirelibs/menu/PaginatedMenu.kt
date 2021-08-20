package com.makeevrserg.empireprojekt.empirelibs.menu

import com.makeevrserg.empireprojekt.EmpirePlugin
import org.bukkit.Material
import org.bukkit.inventory.ItemStack


abstract class PaginatedMenu(playerMenuUtility: PlayerMenuUtility?) : Menu(playerMenuUtility!!) {

    //Page of current menu. Must be 0 by default
    abstract var page:Int
    //Max items allowed in current page. No more than 45 for paginated. Final row is for pagination
    abstract var maxItemsPerPage:Int
    //Max items in section to display
    abstract var slotsAmount:Int
    //Must be owerwritten with getMaxPages()
    abstract var maxPages: Int

    @JvmName("getMaxPages1")
    public fun getMaxPages(): Int {
        return  slotsAmount/maxItemsPerPage
    }

    fun getIndex(i:Int): Int {
        return maxItemsPerPage * page + i
    }

    fun isFirstPage(): Boolean {
        if (page == 0) {
            playerMenuUtility.player
                .sendMessage(EmpirePlugin.translations.FIRST_PAGE)
            return true
        }
        return false
    }
    fun isLastPage(): Boolean {
        if (page >= maxPages) {
            playerMenuUtility.player
                .sendMessage(EmpirePlugin.translations.LAST_PAGE)
            return true
        }
        return false
    }
    fun loadPage(next: Int) {
        page += next
        inventory.clear()
        setMenuItems()
    }

    private fun setManageButton(page: String, id: String?): ItemStack {

        id ?: return ItemStack(Material.PAPER)
        val items = EmpirePlugin.empireItems.empireItems
        val itemStack = items[id] ?: ItemStack(Material.PAPER)
        val itemMeta = itemStack.itemMeta?:return itemStack
        itemMeta.setDisplayName(page)
        itemStack.itemMeta = itemMeta

        return itemStack

    }


    public fun getPrevButtonIndex() = menuSize-8-1
    public fun getBackButtonIndex() = menuSize-4-1
    public fun getNextButtonIndex() = menuSize-1

    fun addManageButtons() {
        if (page >= 1)
            inventory.setItem(
                getPrevButtonIndex(),
                setManageButton(
                    EmpirePlugin.translations.PREV_PAGE,
                    EmpirePlugin.empireFiles.guiFile.getConfig()?.getString("settings.prev_btn")
                )
            )

        inventory.setItem(
            getBackButtonIndex(),
            setManageButton(
                EmpirePlugin.translations.BACK_PAGE,
                EmpirePlugin.empireFiles.guiFile.getConfig()?.getString("settings.back_btn")
            )
        )

        if (page < maxPages)
            inventory.setItem(
                getNextButtonIndex(),
                setManageButton(
                    EmpirePlugin.translations.NEXT_PAGE,
                    EmpirePlugin.empireFiles.guiFile.getConfig()?.getString("settings.next_btn")
                )
            )

    }
}
