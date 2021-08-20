package com.makeevrserg.empireprojekt.empire_items.events.blocks.events

import com.makeevrserg.empireprojekt.empire_items.events.blocks.MushroomBlockApi
import com.makeevrserg.empireprojekt.empirelibs.IEmpireListener
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerInteractEvent

class MushroomBlockTestEvent: IEmpireListener {

    @EventHandler
    fun mushroomBlockInteractEvent(e:PlayerInteractEvent){
        val block = e.clickedBlock?:return
        val data = MushroomBlockApi.getBlockData(block)?:return
    }


    public override fun onDisable() {
        PlayerInteractEvent.getHandlerList().unregister(this)
    }

}