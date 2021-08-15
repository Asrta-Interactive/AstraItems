package com.makeevrserg.empireprojekt.events.blocks.events

import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.events.blocks.MushroomBlockApi
import empirelibs.IEmpireListener
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDamageEvent
import org.bukkit.event.player.PlayerAnimationEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent

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