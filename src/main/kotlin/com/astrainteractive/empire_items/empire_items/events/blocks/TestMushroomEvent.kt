package com.astrainteractive.empire_items.empire_items.events.blocks

import com.astrainteractive.astralibs.async.AsyncHelper
import com.astrainteractive.astralibs.async.AsyncTask
import com.astrainteractive.astralibs.events.DSLEvent
import com.astrainteractive.empire_items.api.EmpireItemsAPI
import com.astrainteractive.empire_items.api.items.BlockParser
import kotlinx.coroutines.*
import org.bukkit.Location
import org.bukkit.event.block.BlockPhysicsEvent
import org.bukkit.event.player.PlayerInteractEvent

class TestMushroomEvent {
    val blockPhysicEvent = DSLEvent.event(PlayerInteractEvent::class.java) { e ->
        return@event
        val debris = EmpireItemsAPI.itemYamlFilesByID["end_debris"]!!
        val faces = BlockParser.getFacingByData(debris.block?.data!!)
        val type = BlockParser.getMaterialByData(debris.block?.data!!)
        val l = e.clickedBlock?.location ?: return@event
            val blocks = IntRange(l.x.toInt(), l.x.toInt() + 50).flatMap { x ->
                IntRange(l.y.toInt(), l.y.toInt() + 50).flatMap { y ->
                    IntRange(l.z.toInt(), l.z.toInt() + 50).map { z ->
                        val block = Location(l.world, x.toDouble(), y.toDouble(), z.toDouble()).block
                        block
//                        BlockParser.setTypeFast(block, type, faces)
                    }
                }
            }
            BlockParser.setTypeFast(blocks, type, faces, debris.block?.data)

    }
}