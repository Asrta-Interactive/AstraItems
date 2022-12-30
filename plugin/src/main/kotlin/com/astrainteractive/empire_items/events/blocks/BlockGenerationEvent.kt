package com.astrainteractive.empire_items.events.blocks


import com.astrainteractive.empire_items.di.blockGenerationApiModule
import io.papermc.paper.event.packet.PlayerChunkLoadEvent
import org.bukkit.event.world.ChunkLoadEvent
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astralibs.events.DSLEvent


class BlockGenerationEvent {
    private val blockGenerationModule by blockGenerationApiModule

    val chunkLoadEvent = DSLEvent.event(ChunkLoadEvent::class.java) { e ->
        blockGenerationModule.validateChunk(e.chunk, e.isNewChunk)
    }
    val playerChunkLoadEvent = DSLEvent.event(PlayerChunkLoadEvent::class.java) { e ->
        blockGenerationModule.validateChunk(e.chunk, false)
    }
}

