package com.astrainteractive.empireprojekt.empire_items.events.blocks.events

import com.astrainteractive.astralibs.IAstraListener
import com.astrainteractive.empireprojekt.EmpirePlugin
import com.astrainteractive.empireprojekt.empire_items.api.ItemsAPI
import com.astrainteractive.empireprojekt.empire_items.api.MushroomBlockApi
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
        val data = MushroomBlockApi.getBlockData(block)?:return

        val id = ItemsAPI.getEmpireBlockIdByData(data)?:return

        if (EmpirePlugin.dropManager.itemDrops[id]!=null)
            return

        e.isDropItems=false
        val listDrop = EmpirePlugin.dropManager.everyDropByItem[id]//itemDrops[id?:block.blockData.material.name] ?: return
		
        block.location.world?.dropItem(block.location,ItemsAPI.getEmpireItemStack(id)?:return)?:return
    }
}