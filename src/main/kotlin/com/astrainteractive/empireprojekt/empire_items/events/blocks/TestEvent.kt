package com.astrainteractive.empireprojekt.empire_items.events.blocks

import com.astrainteractive.astralibs.IAstraListener
import com.astrainteractive.empireprojekt.empire_items.api.items.BlockParser
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerInteractEvent

class TestEvent :IAstraListener {

    @EventHandler
    fun testEvent(e:PlayerInteractEvent){
        val block = e.clickedBlock?:return
        BlockParser.setTypeFast(block,BlockParser.getMaterialByData(9),BlockParser.getFacingByData(9))
    }
    override fun onDisable() {
        PlayerInteractEvent.getHandlerList().unregister(this)
    }
}