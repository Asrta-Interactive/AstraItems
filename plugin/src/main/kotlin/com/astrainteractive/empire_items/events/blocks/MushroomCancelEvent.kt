package com.astrainteractive.empire_items.events.blocks

import com.astrainteractive.empire_items.api.items.BlockParser
import org.bukkit.event.block.BlockPhysicsEvent
import ru.astrainteractive.astralibs.events.DSLEvent

class MushroomCancelEvent{
    val blockPhysicEvent = DSLEvent.event<BlockPhysicsEvent>  { e ->
        BlockParser.getMultipleFacing(e.block)?:return@event
        e.isCancelled = true
    }
}