package com.astrainteractive.empire_items.events.blocks

import com.astrainteractive.empire_items.di.empireItemsApiModule
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.events.DSLEvent
import com.astrainteractive.empire_itemss.api.EmpireItemsAPI
import com.astrainteractive.empire_itemss.api.empireID
import com.astrainteractive.empire_itemss.api.items.BlockParser
import kotlinx.coroutines.launch

import org.bukkit.event.block.BlockPlaceEvent
import ru.astrainteractive.astralibs.di.getValue

class MushroomBlockPlaceEvent {
    private val empireItemsAPI by empireItemsApiModule
    val blockPlace = DSLEvent.event(BlockPlaceEvent::class.java) { e ->

        if (e.isCancelled) return@event
        val player = e.player
        val block = e.block
        val id = player.inventory.itemInMainHand.empireID ?: return@event
        val empireBlock = empireItemsAPI.itemYamlFilesByID[id]?.block ?: return@event
        val facing = BlockParser.getFacingByData(empireBlock.data)
        val type = BlockParser.getMaterialByData(empireBlock.data)
        PluginScope.launch {
            BlockParser.setTypeFast(block, type, facing, empireBlock.data)
        }
    }

}