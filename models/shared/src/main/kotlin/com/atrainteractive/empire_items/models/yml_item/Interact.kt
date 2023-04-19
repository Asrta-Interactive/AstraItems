package com.atrainteractive.empire_items.models.yml_item

import kotlinx.serialization.Serializable
@Suppress("PROVIDED_RUNTIME_TOO_LOW")
@Serializable
data class Interact(
    val eventList: List<String>,
    val cooldown: Int? = null,
    val playParticle: Map<String, PlayParticle> = mapOf(),
    val playSound: Map<String, PlaySound> = mapOf(),
    val playCommand: Map<String, PlayCommand> = mapOf(),
    val playPotionEffect: Map<String, PlayPotionEffect> = mapOf(),
    val removePotionEffect: List<String> = listOf(),
) {
    @Suppress("PROVIDED_RUNTIME_TOO_LOW")
    @Serializable
    data class PlayParticle(
        val name: String,
        val count: Int = 20,
        val time: Double = 0.1,
        val color: String? = null,
        val extra: Double = 0.0,
    ) {


    }

    @Suppress("PROVIDED_RUNTIME_TOO_LOW")
    @Serializable
    data class PlaySound(
        val name: String,
        val pitch: Float = 1f,
        val volume: Float = 1f,
        val cooldown: Int? = null,
    )

    @Suppress("PROVIDED_RUNTIME_TOO_LOW")
    @Serializable
    data class PlayCommand(
        val command: String,
        val asConsole: Boolean = false,
    )

    @Suppress("PROVIDED_RUNTIME_TOO_LOW")
    @Serializable
    data class PlayPotionEffect(
        val name: String,
        val amplifier: Int = 1,
        val duration: Int = 200,
        val display: Boolean = true,
    )
}