package com.astrainteractive.empire_items.events.blocks

import com.astrainteractive.empire_items.di.empireItemsApiModule
import ru.astrainteractive.astralibs.events.DSLEvent
import com.astrainteractive.empire_itemss.api.EmpireItemsAPI
import com.astrainteractive.empire_itemss.api.items.BlockParser
import org.bukkit.event.block.BlockBreakEvent
import ru.astrainteractive.astralibs.di.getValue

class MushroomBlockBreakEvent {
    private val empireItemsAPI by empireItemsApiModule
    val blockBreak = DSLEvent.event(BlockBreakEvent::class.java)  { e ->
        if (e.isCancelled)
            return@event

//        if (!KProtectionLib.canBuild(e.player, e.block.location)) return@event

//        if (!KProtectionLib.canBreak(e.player, e.block.location)) return@event
        if (e.isCancelled) return@event
        val player = e.player
        val block = e.block
        val data = BlockParser.getBlockData(block)?:return@event
        val id = empireItemsAPI.itemYamlFilesByID.values.firstOrNull { it.block?.data==data }?.id?:return@event
        e.isDropItems=false
    }
}