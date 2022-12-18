package com.astrainteractive.empire_items.events.blocks


import com.astrainteractive.empire_items.di.blockGenerationApiModule
import io.papermc.paper.event.packet.PlayerChunkLoadEvent
import kotlinx.coroutines.*
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.world.ChunkLoadEvent
import org.jetbrains.kotlin.gradle.utils.`is`
import ru.astrainteractive.astralibs.Logger
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astralibs.events.DSLEvent
import java.util.UUID


class BlockGenerationEvent {
    private val blockGenerationModule by blockGenerationApiModule

    val chunkLoadEvent = DSLEvent.event(ChunkLoadEvent::class.java) { e ->
        PluginScope.launch(Dispatchers.IO) { blockGenerationModule.validateChunk(e.chunk, e.isNewChunk) }
    }
    val playerChunkLoadEvent = DSLEvent.event(PlayerChunkLoadEvent::class.java) { e ->
        PluginScope.launch(Dispatchers.IO) { blockGenerationModule.validateChunk(e.chunk, false) }
    }
}

