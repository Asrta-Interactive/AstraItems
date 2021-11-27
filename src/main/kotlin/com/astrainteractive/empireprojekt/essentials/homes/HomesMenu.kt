//package com.astrainteractive.empireprojekt.essentials.homes
//
//import com.earth2me.essentials.User
//import com.astrainteractive.empireprojekt.astralibs.menu.AbstractPaginatedMenu
//import com.astrainteractive.empireprojekt.astralibs.menu.PlayerMenuUtility
//import org.bukkit.Material
//import org.bukkit.event.inventory.InventoryClickEvent
//import org.bukkit.inventory.ItemStack
//
//class HomesMenu(override val playerMenuUtility: PlayerMenuUtility): AbstractPaginatedMenu(playerMenuUtility) {
//    override var menuName: String = "Ваши дома"
//    override val menuSize: Int = 18
//    override var maxItemsPerPage = 9
//    private var user:User? = EssentialsHandler.ess?.getUser(playerMenuUtility.player)
//    override var slotsAmount: Int = user?.homes?.size?:0
//    override var maxPages: Int = getMaxPages()
//    override var page: Int = 0
//
//
//    init {
//        if (EssentialsHandler.ess == null)
//            playerMenuUtility.player.closeInventory()
//
//    }
//
//    override fun handleMenu(e: InventoryClickEvent) {
//        e.currentItem ?: return
//        when (e.slot){
//            maxItemsPerPage->{
//                loadPage(-1)
//                return
//            }
//            maxItemsPerPage+4->{
//                playerMenuUtility.player.closeInventory()
//                return
//            }
//            maxItemsPerPage+8->{
//                loadPage(+1)
//                return
//            }
//        }
//        val index = maxItemsPerPage*page + e.slot
//        playerMenuUtility.player.closeInventory()
//        playerMenuUtility.player.teleport(user!!.getHome(user!!.homes[index]))
//    }
//
//    override fun setMenuItems() {
//        addManageButtons()
//        for (position in 0 until maxItemsPerPage){
//            val index = maxItemsPerPage*page+position
//            if (index>=user!!.homes.size)
//                return
//            val homeName = user!!.homes[index]
//            val itemStack = ItemStack(Material.BLUE_BED)
//            val itemMeta = itemStack.itemMeta?:continue
//            itemMeta.setDisplayName(homeName)
//            itemStack.itemMeta = itemMeta
//            inventory.setItem(position,itemStack)
//        }
//
//
//    }
//
//
//}