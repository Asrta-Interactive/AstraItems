package com.astrainteractive.empire_items.empire_items.events.blocks

import com.astrainteractive.astralibs.IAstraListener
import com.astrainteractive.empire_items.empire_items.api.items.BlockParser
import com.astrainteractive.empire_items.empire_items.api.items.data.ItemManager
import com.astrainteractive.empire_items.empire_items.api.items.data.ItemManager.getAstraID

import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockPlaceEvent

class MushroomBlockPlaceEvent: IAstraListener {
    public override fun onDisable(){
        BlockPlaceEvent.getHandlerList().unregister(this)
    }
    @EventHandler
    fun blockPlace(e:BlockPlaceEvent){
        val player = e.player
        val block = e.block
        val id = player.inventory.itemInMainHand.getAstraID()?:return
        val empireBlock = ItemManager.getItemInfo(id)?.block?:return
        val facing = BlockParser.getFacingByData(empireBlock.data)
        val type = BlockParser.getMaterialByData(empireBlock.data)
        BlockParser.setTypeFast(block,type,facing)
    }

}