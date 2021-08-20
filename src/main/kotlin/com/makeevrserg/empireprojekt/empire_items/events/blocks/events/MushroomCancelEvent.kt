package com.makeevrserg.empireprojekt.empire_items.events.blocks.events

import com.makeevrserg.empireprojekt.empire_items.events.blocks.MushroomBlockApi
import com.makeevrserg.empireprojekt.empirelibs.IEmpireListener
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockPhysicsEvent

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