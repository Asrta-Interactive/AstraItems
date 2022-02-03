package com.astrainteractive.empire_items.empire_items.events.blocks

import com.astrainteractive.astralibs.EventListener
import com.astrainteractive.empire_items.empire_items.api.items.BlockParser
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockPhysicsEvent

class MushroomCancelEvent: EventListener {

    @EventHandler
    fun blockPhysicEvent(e:BlockPhysicsEvent){
        BlockParser.getMultipleFacing(e.block)?:return
        e.isCancelled = true
    }


    public override fun onDisable(){
        BlockPhysicsEvent.getHandlerList().unregister(this)
    }
}