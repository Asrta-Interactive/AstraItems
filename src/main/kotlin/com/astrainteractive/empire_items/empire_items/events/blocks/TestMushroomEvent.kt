package com.astrainteractive.empire_items.empire_items.events.blocks

import com.astrainteractive.astralibs.events.DSLEvent
import com.astrainteractive.empire_items.api.EmpireItemsAPI
import com.astrainteractive.empire_items.api.items.BlockParser
import org.bukkit.event.block.BlockPhysicsEvent
import org.bukkit.event.player.PlayerInteractEvent

class TestMushroomEvent {
    val blockPhysicEvent = DSLEvent.event(PlayerInteractEvent::class.java) { e ->
        val debris = EmpireItemsAPI.itemYamlFilesByID["end_debris"]!!
        val faces = BlockParser.getFacingByData(debris.block?.data!!)
        val type = BlockParser.getMaterialByData(debris.block.data)
        e.clickedBlock?.let {
            BlockParser.setTypeFast(it,type,faces)
        }
    }
}