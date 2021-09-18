package com.makeevrserg.empireprojekt.empire_items.events.decorations

import com.makeevrserg.empireprojekt.empire_items.api.DecorationBlockAPI
import com.makeevrserg.empireprojekt.empire_items.api.ItemsAPI.getEmpireID
import com.makeevrserg.empireprojekt.empirelibs.IEmpireListener
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot

class DecorationBlockPlaceEvent : IEmpireListener {

    @EventHandler
    fun blockPlaceEvent(e: BlockPlaceEvent) {
        val player = e.player
        val itemStack = player.inventory.itemInMainHand.clone()
        val itemId = itemStack.getEmpireID() ?: return
        val location = e.blockPlaced.location.clone()
        if (e.player.isSneaking)
            DecorationBlockAPI.placeBlock(itemId,location,player.location,true)
        else
            DecorationBlockAPI.placeBlock(itemId,location,player.location)
    }
    @EventHandler
    fun decorationInteractEvent(e:PlayerInteractEvent){
        val player = e.player
        val block = e.clickedBlock?:return
        if (block.type!=Material.BARRIER)
            return
        if (e.action==Action.LEFT_CLICK_BLOCK && e.hand==EquipmentSlot.HAND)
            DecorationBlockAPI.breakItem(block.location)




    }



    override fun onDisable() {
        BlockPlaceEvent.getHandlerList().unregister(this)
        PlayerInteractEvent.getHandlerList().unregister(this)

    }
}