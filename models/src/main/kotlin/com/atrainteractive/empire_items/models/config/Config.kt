package com.atrainteractive.empire_items.models.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Suppress("PROVIDED_RUNTIME_TOO_LOW")
@kotlinx.serialization.Serializable
data class Config(
    @SerialName("resource_pack")
    val resourcePack: ResourcePackConfig,
    val generation: GenerationConfig,
    val tabPrefix: String,
    @SerialName("arena_command")
    val arenaCommand: ArenaCommand
) {
    @Suppress("PROVIDED_RUNTIME_TOO_LOW")
    @Serializable
    data class ArenaCommand(
        val mobID:String,
        @SerialName("players_location")
        val playersLocation: Location,
        @SerialName("boss_location")
        val bossLocation: Location,
        @SerialName("spawn_location")
        val spawnLocation: Location,
        @SerialName("boss_spawn_delay")
        val bossSpawnDelay: Long = 0,
        @SerialName("players_teleport_delay")
        val playersTeleportDelay: Long = 0,
        val itemID:String,
        val radius:Double = 10.0,
    ) {
        @Suppress("PROVIDED_RUNTIME_TOO_LOW")
        @Serializable
        data class Location(val x: Double, val y: Double, val z: Double,val world:String)
    }

    @Suppress("PROVIDED_RUNTIME_TOO_LOW")
    @Serializable
    data class GenerationConfig(
        val debug: Boolean,
        val enabled: Boolean,
        val onlyOnNewChunks: Boolean,
        val generateChunksAtOnce: Int,
    )

    @Suppress("PROVIDED_RUNTIME_TOO_LOW")
    @Serializable
    data class ResourcePackConfig(
        val link: String,
        val requestOnJoin: Boolean,
        val kickOnDeny: Boolean,
        val requestDelay: Int
    )

}
