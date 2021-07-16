package com.makeevrserg.empireprojekt.events.blocks.events

import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.events.blocks.MushroomBlockApi
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPhysicsEvent
import org.bukkit.event.block.BlockPlaceEvent

class MushroomCancelEvent:Listener {

    @EventHandler
    fun blockPhysicEvent(e:BlockPhysicsEvent){
        MushroomBlockApi.getMultipleFacing(e.block)?:return
        e.isCancelled = true
    }

    init {
        EmpirePlugin.instance.server.pluginManager.registerEvents(this, EmpirePlugin.instance)
    }
    public fun onDisable(){
        BlockPhysicsEvent.getHandlerList().unregister(this)
    }
}