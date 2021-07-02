package com.makeevrserg.empireprojekt.essentials.homes

import com.earth2me.essentials.User
import com.makeevrserg.empireprojekt.menumanager.PaginatedMenu
import com.makeevrserg.empireprojekt.menumanager.PlayerMenuUtility
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class HomesMenu(playerMenuUtility: PlayerMenuUtility):PaginatedMenu(playerMenuUtility) {
    override var menuName: String = "Ваши дома"
    override val slots: Int = 18
    override var maxItemsPerPage = 9
    private lateinit var user:User


    private fun onEnable(){
        if (EssentialsHandler.ess == null){
            playerMenuUtility.player.closeInventory()
            return
        }
        user = EssentialsHandler.ess!!.getUser(playerMenuUtility.player)
        maxPages = user.homes.size/maxItemsPerPage
    }
    init {
        onEnable()
    }

    override fun handleMenu(e: InventoryClickEvent) {
        e.currentItem ?: return
        when (e.slot){
            maxItemsPerPage->{
                reloadPage(-1)
                return
            }
            maxItemsPerPage+4->{
                playerMenuUtility.player.closeInventory()
                return
            }
            maxItemsPerPage+8->{
                reloadPage(+1)
                return
            }
        }
        val index = maxItemsPerPage*page + e.slot
        playerMenuUtility.player.closeInventory()
        playerMenuUtility.player.teleport(user.getHome(user.homes[index]))
    }

    override fun setMenuItems() {
        addManageButtons()
        for (position in 0 until maxItemsPerPage){
            val index = maxItemsPerPage*page+position
            if (index>=user.homes.size)
                return
            val homeName = user.homes[index]
            val home = user.getHome(homeName)
            val itemStack = ItemStack(Material.BLUE_BED)
            val itemMeta = itemStack.itemMeta?:continue
            itemMeta.setDisplayName(homeName)
            itemStack.itemMeta = itemMeta
            inventory.setItem(position,itemStack)
        }


    }


}