package com.astrainteractive.empire_items.events.blocks

import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.events.DSLEvent
import com.astrainteractive.empire_itemss.api.EmpireItemsAPI
import com.astrainteractive.empire_itemss.api.EmpireItemsAPI.empireID
import com.astrainteractive.empire_itemss.api.items.BlockParser
import kotlinx.coroutines.launch

import org.bukkit.event.block.BlockPlaceEvent

class MushroomBlockPlaceEvent {
    val blockPlace = DSLEvent.event(BlockPlaceEvent::class.java) { e ->

        if (e.isCancelled) return@event
        val player = e.player
        val block = e.block
        val id = player.inventory.itemInMainHand.empireID ?: return@event
        val empireBlock = EmpireItemsAPI.itemYamlFilesByID[id]?.block ?: return@event
        val facing = BlockParser.getFacingByData(empireBlock.data)
        val type = BlockParser.getMaterialByData(empireBlock.data)
        PluginScope.launch {
            BlockParser.setTypeFast(block, type, facing, empireBlock.data)
        }
    }

}