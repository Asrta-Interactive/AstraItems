package com.astrainteractive.empire_items.empire_items.events.blocks

import ru.astrainteractive.astralibs.events.DSLEvent
import ru.astrainteractive.astralibs.events.EventListener
import com.astrainteractive.empire_items.api.EmpireItemsAPI
import com.astrainteractive.empire_items.api.items.BlockParser
import com.astrainteractive.empire_items.empire_items.util.protection.KProtectionLib
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPhysicsEvent

class MushroomBlockBreakEvent {
    val blockBreak = DSLEvent.event(BlockBreakEvent::class.java)  { e ->
        if (e.isCancelled)
            return@event

//        if (!KProtectionLib.canBuild(e.player, e.block.location)) return@event

//        if (!KProtectionLib.canBreak(e.player, e.block.location)) return@event
        if (e.isCancelled) return@event
        val player = e.player
        val block = e.block
        val data = BlockParser.getBlockData(block)?:return@event
        val id = EmpireItemsAPI.itemYamlFilesByID.values.firstOrNull { it.block?.data==data }?.id?:return@event
        e.isDropItems=false
    }
}