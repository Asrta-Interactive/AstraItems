package com.astrainteractive.empireprojekt.essentials.homes

import com.astrainteractive.empireprojekt.astralibs.menu.AbstractPaginatedMenu
import com.astrainteractive.empireprojekt.astralibs.menu.PlayerMenuUtility
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class WarpsMenu(override val playerMenuUtility: PlayerMenuUtility) : AbstractPaginatedMenu(playerMenuUtility) {
    override var menuName: String = "Варпы"
    override val menuSize: Int = 18
    override var maxItemsPerPage = 9
    var warpNames: Collection<String>? = EssentialsHandler.ess?.warps?.list
    override var slotsAmount: Int =  warpNames?.size?:0
    override var maxPages: Int =getMaxPages()
    override var page: Int = 0


    init {
        if (EssentialsHandler.ess == null)
            playerMenuUtility.player.closeInventory()

    }

    override fun handleMenu(e: InventoryClickEvent) {
        e.currentItem ?: return
        when (e.slot) {
            maxItemsPerPage -> {
                loadPage(-1)
                return
            }
            maxItemsPerPage + 4 -> {
                playerMenuUtility.player.closeInventory()
                return
            }
            maxItemsPerPage + 8 -> {
                loadPage(+1)
                return
            }
        }
        val index = maxItemsPerPage * page + e.slot
        playerMenuUtility.player.closeInventory()
        playerMenuUtility.player.teleport(EssentialsHandler.ess!!.warps.getWarp(warpNames!!.elementAt(index)))
    }

    override fun setMenuItems() {
        addManageButtons()
        for (position in 0 until maxItemsPerPage) {
            val index = maxItemsPerPage * page + position
            if (index >= warpNames!!.size)
                return
            val warpName = warpNames!!.elementAt(index)
            val itemStack = ItemStack(Material.BEACON)
            val itemMeta = itemStack.itemMeta ?: continue
            itemMeta.setDisplayName(warpName)
            itemStack.itemMeta = itemMeta
            inventory.setItem(position, itemStack)
        }


    }


}