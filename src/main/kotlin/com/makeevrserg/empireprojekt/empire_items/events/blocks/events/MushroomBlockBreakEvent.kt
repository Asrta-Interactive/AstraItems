package com.makeevrserg.empireprojekt.empire_items.events.blocks.events

import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.empire_items.api.MushroomBlockApi
import com.makeevrserg.empireprojekt.empirelibs.IEmpireListener
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent

class MushroomBlockBreakEvent:IEmpireListener {



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

        val id = EmpirePlugin.empireItems.empireBlocksByData[data]?:return

        if (EmpirePlugin.dropManager.itemDrops[id]!=null)
            return

        e.isDropItems=false
        val listDrop = EmpirePlugin.dropManager.everyDropByItem[id]//itemDrops[id?:block.blockData.material.name] ?: return
		
        block.location.world?.dropItem(block.location,EmpirePlugin.empireItems.empireItems[id]?:return)?:return
    }
}