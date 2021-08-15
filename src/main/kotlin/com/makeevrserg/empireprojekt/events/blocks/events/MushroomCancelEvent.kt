package com.makeevrserg.empireprojekt.events.blocks.events

import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.events.blocks.MushroomBlockApi
import empirelibs.IEmpireListener
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPhysicsEvent
import org.bukkit.event.block.BlockPlaceEvent

class MushroomCancelEvent:IEmpireListener {

    @EventHandler
    fun blockPhysicEvent(e:BlockPhysicsEvent){
        MushroomBlockApi.getMultipleFacing(e.block)?:return
        e.isCancelled = true
    }


    public override fun onDisable(){
        BlockPhysicsEvent.getHandlerList().unregister(this)
    }
}