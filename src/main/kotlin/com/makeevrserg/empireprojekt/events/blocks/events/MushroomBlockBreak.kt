package com.makeevrserg.empireprojekt.events.blocks.events

import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.events.blocks.MushroomBlockApi
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent

class MushroomBlockBreak:Listener {

    init {
        EmpirePlugin.instance.server.pluginManager.registerEvents(this, EmpirePlugin.instance)
    }
    public fun onDisable(){
        BlockBreakEvent.getHandlerList().unregister(this)
    }

    @EventHandler
    fun blockBreak(e:BlockBreakEvent){
        val player = e.player
        val block = e.block
        val data = MushroomBlockApi.getBlockData(block)?:return

        val id = EmpirePlugin.empireItems._empireBlocksByData[data]?:return

        e.isDropItems=false
        block.location.world?.dropItem(block.location,EmpirePlugin.empireItems.empireItems[id]?:return)?:return
    }
}