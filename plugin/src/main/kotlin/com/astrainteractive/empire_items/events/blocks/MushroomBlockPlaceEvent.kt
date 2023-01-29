package com.astrainteractive.empire_items.events.blocks

import com.astrainteractive.empire_items.di.blockPlacerModule
import com.astrainteractive.empire_items.di.empireItemsApiModule
import com.astrainteractive.empire_items.api.utils.empireID
import com.astrainteractive.empire_items.api.items.BlockParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bukkit.event.block.BlockPlaceEvent
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astralibs.events.DSLEvent

class MushroomBlockPlaceEvent {
    private val empireItemsAPI by empireItemsApiModule
    private val blockPlacer by blockPlacerModule
    val blockPlace = DSLEvent.event<BlockPlaceEvent> { e ->

        if (e.isCancelled) return@event
        val player = e.player
        val block = e.block
        val id = player.inventory.itemInMainHand.empireID ?: return@event
        val empireBlock = empireItemsAPI.itemYamlFilesByID[id]?.block ?: return@event
        val facing = BlockParser.getFacingByData(empireBlock.data)
        val type = BlockParser.getMaterialByData(empireBlock.data)
        PluginScope.launch(Dispatchers.IO) {
            blockPlacer.setTypeFast(type, facing, empireBlock.data, block)
        }
    }

}