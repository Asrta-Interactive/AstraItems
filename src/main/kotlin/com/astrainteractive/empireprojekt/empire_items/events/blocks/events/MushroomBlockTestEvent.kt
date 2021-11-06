package com.astrainteractive.empireprojekt.empire_items.events.blocks.events

import com.astrainteractive.astralibs.IAstraListener
import com.astrainteractive.empireprojekt.empire_items.api.MushroomBlockApi
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerInteractEvent

class MushroomBlockTestEvent: IAstraListener {

    @EventHandler
    fun mushroomBlockInteractEvent(e:PlayerInteractEvent){
        val block = e.clickedBlock?:return
        val data = MushroomBlockApi.getBlockData(block)?:return
    }


    public override fun onDisable() {
        PlayerInteractEvent.getHandlerList().unregister(this)
    }

}