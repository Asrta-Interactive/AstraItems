package com.astrainteractive.empire_items.empire_items.events.decoration

import com.astrainteractive.astralibs.EventListener
import com.astrainteractive.empire_items.api.items.DecorationBlockAPI
import com.astrainteractive.empire_items.api.items.data.ItemApi.getAstraID
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot

class DecorationEvent:EventListener {

    @EventHandler
    fun blockPlaceEvent(e: BlockPlaceEvent) {
        val player = e.player
        val itemStack = player.inventory.itemInMainHand.clone()
        val itemId = itemStack.getAstraID() ?: return
        val location = e.blockPlaced.location.clone()
            DecorationBlockAPI.placeBlock(itemId,location,player.location)
    }
    @EventHandler
    fun decorationInteractEvent(e:PlayerInteractEvent){
        val player = e.player
        val block = e.clickedBlock?:return
        if (block.type!= Material.BARRIER)
            return

        if ( e.action== Action.LEFT_CLICK_BLOCK && e.hand== EquipmentSlot.HAND)
            DecorationBlockAPI.breakItem(block.location)





    }


    override fun onDisable() {
    }
}