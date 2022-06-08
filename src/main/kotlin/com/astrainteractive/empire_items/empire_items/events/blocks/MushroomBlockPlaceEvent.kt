package com.astrainteractive.empire_items.empire_items.events.blocks

import com.astrainteractive.astralibs.events.DSLEvent
import com.astrainteractive.astralibs.events.EventListener
import com.astrainteractive.empire_items.api.EmpireItemsAPI
import com.astrainteractive.empire_items.api.EmpireItemsAPI.empireID
import com.astrainteractive.empire_items.api.items.BlockParser

import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent

class MushroomBlockPlaceEvent{
    val blockPlace = DSLEvent.event(BlockPlaceEvent::class.java)  { e ->
        val player = e.player
        val block = e.block
        val id = player.inventory.itemInMainHand.empireID?:return@event
        val empireBlock = EmpireItemsAPI.itemYamlFilesByID[id]?.block?:return@event
        val facing = BlockParser.getFacingByData(empireBlock.data)
        val type = BlockParser.getMaterialByData(empireBlock.data)
        BlockParser.setTypeFast(block,type,facing)
    }

}