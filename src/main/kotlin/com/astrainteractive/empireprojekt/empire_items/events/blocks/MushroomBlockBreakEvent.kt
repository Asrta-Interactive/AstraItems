package com.astrainteractive.empireprojekt.empire_items.events.blocks

import com.astrainteractive.astralibs.IAstraListener
import com.astrainteractive.empireprojekt.empire_items.api.items.BlockParser
import com.astrainteractive.empireprojekt.empire_items.api.items.data.ItemManager
import com.astrainteractive.empireprojekt.empire_items.api.items.data.ItemManager.getItemStack
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent

class MushroomBlockBreakEvent: IAstraListener {



    public override fun onDisable(){
        BlockBreakEvent.getHandlerList().unregister(this)
    }

    @EventHandler
    fun blockBreak(e:BlockBreakEvent){
        if (e.isCancelled)
            return
        val player = e.player
        val block = e.block
        val data = BlockParser.getBlockData(block)?:return

        val id = ItemManager.getBlockInfoByData(data)?.id?:return
        val itemStack = id.getItemStack()?:return
        e.isDropItems=false
//        block.location.world?.dropItem(block.location,itemStack)
    }
}