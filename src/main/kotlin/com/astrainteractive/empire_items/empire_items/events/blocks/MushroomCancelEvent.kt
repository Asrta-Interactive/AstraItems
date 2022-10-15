package com.astrainteractive.empire_items.empire_items.events.blocks

import ru.astrainteractive.astralibs.events.DSLEvent
import ru.astrainteractive.astralibs.events.EventListener
import com.astrainteractive.empire_items.api.items.BlockParser
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockPhysicsEvent
import org.bukkit.event.hanging.HangingPlaceEvent

class MushroomCancelEvent{
    val blockPhysicEvent = DSLEvent.event(BlockPhysicsEvent::class.java)  { e ->
        BlockParser.getMultipleFacing(e.block)?:return@event
        e.isCancelled = true
    }
}