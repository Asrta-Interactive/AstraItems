package com.astrainteractive.empire_items.events.blocks

import ru.astrainteractive.astralibs.events.DSLEvent
import com.astrainteractive.empire_itemss.api.items.BlockParser
import org.bukkit.event.block.BlockPhysicsEvent

class MushroomCancelEvent{
    val blockPhysicEvent = DSLEvent.event(BlockPhysicsEvent::class.java)  { e ->
        BlockParser.getMultipleFacing(e.block)?:return@event
        e.isCancelled = true
    }
}