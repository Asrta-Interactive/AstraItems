package com.makeevrserg.empireprojekt.events.blocks.events

import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.events.blocks.MushroomBlockApi
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDamageEvent
import org.bukkit.event.player.PlayerAnimationEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent

class MushroomBlockTestEvent: Listener {

    @EventHandler
    fun mushroomBlockInteractEvent(e:PlayerInteractEvent){
        val block = e.clickedBlock?:return
        val data = MushroomBlockApi.getBlockData(block)?:return
    }

    init {
        EmpirePlugin.instance.server.pluginManager.registerEvents(this, EmpirePlugin.instance)
    }

    public fun onDisable() {
        PlayerInteractEvent.getHandlerList().unregister(this)
    }

}