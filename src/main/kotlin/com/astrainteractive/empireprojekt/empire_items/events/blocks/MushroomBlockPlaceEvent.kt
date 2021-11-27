package com.astrainteractive.empireprojekt.empire_items.events.blocks

import com.astrainteractive.astralibs.IAstraListener
import com.astrainteractive.empireprojekt.empire_items.api.items.BlockParser
import com.astrainteractive.empireprojekt.empire_items.api.items.data.ItemManager
import com.astrainteractive.empireprojekt.empire_items.api.items.data.ItemManager.getAstraID

import org.bukkit.block.BlockFace
import org.bukkit.block.data.MultipleFacing
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
        val empireFacings = BlockParser.getFacingByData(empireBlock.data)
        block.type = BlockParser.getMaterialByData(empireBlock.data)
        val facing = block.blockData as MultipleFacing
        for (f in empireFacings)
            facing.setFace(BlockFace.valueOf(f.key.uppercase()),f.value)
        e.block.blockData = facing
    }

}