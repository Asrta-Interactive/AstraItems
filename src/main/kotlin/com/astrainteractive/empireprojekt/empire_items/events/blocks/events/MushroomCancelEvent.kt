package com.astrainteractive.empireprojekt.empire_items.events.blocks.events

import com.astrainteractive.astralibs.IAstraListener
import com.astrainteractive.empireprojekt.empire_items.api.MushroomBlockApi
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockPhysicsEvent

class MushroomCancelEvent: IAstraListener {

    @EventHandler
    fun blockPhysicEvent(e:BlockPhysicsEvent){
        MushroomBlockApi.getMultipleFacing(e.block)?:return
        e.isCancelled = true
    }


    public override fun onDisable(){
        BlockPhysicsEvent.getHandlerList().unregister(this)
    }
}