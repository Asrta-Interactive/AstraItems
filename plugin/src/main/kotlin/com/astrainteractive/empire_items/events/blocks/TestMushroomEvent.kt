package com.astrainteractive.empire_items.events.blocks

import com.astrainteractive.empire_items.di.blockGenerationApiModule
import com.astrainteractive.empire_items.di.blockPlacerModule
import com.astrainteractive.empire_items.di.empireItemsApiModule
import com.astrainteractive.empire_itemss.api.items.BlockParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bukkit.Location
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astralibs.events.DSLEvent

class TestMushroomEvent {
    private val empireItemsAPI by empireItemsApiModule
    private val blockPlacer by blockPlacerModule
    private val blockGenerationModule by blockGenerationApiModule
    val blockPhysicEvent = DSLEvent.event<PlayerInteractEvent> { e ->
        if (!e.player.hasPermission("sadsad.asdnjsadj.asjdas")) return@event
        if (e.action == Action.RIGHT_CLICK_BLOCK) {
            blockGenerationModule.validateChunk(e.player.location.chunk, true)
            return@event
        }
        else if (e.action != Action.LEFT_CLICK_BLOCK) return@event

        if (e.hand != EquipmentSlot.HAND) return@event
        val debris = empireItemsAPI.itemYamlFilesByID["end_debris"]!!
        val faces = BlockParser.getFacingByData(debris.block?.data!!)
        val type = BlockParser.getMaterialByData(debris.block?.data!!)
        val l = e.clickedBlock?.location ?: return@event
        PluginScope.launch(Dispatchers.IO) {
            val blocks = IntRange(l.x.toInt(), l.x.toInt() + 50).flatMap { x ->
                IntRange(l.y.toInt(), l.y.toInt() + 50).flatMap { y ->
                    IntRange(l.z.toInt(), l.z.toInt() + 50).map { z ->
                        val block = Location(l.world, x.toDouble(), y.toDouble(), z.toDouble()).block
                        block
                    }
                }
            }
            blockPlacer.setTypeFast(type, faces, debris.block?.data, *blocks.toTypedArray())
        }

    }
}