package com.astrainteractive.empire_items.models.yml_item

import kotlinx.serialization.Serializable
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.entity.LivingEntity
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType


@Suppress("PROVIDED_RUNTIME_TOO_LOW")
@Serializable
data class Interact(
    val eventList: List<String>,
    val cooldown: Int? = null,
    val playParticle: Map<String, PlayParticle> = mapOf(),
    val playSound: Map<String, PlaySound> = mapOf(),
    val playCommand: Map<String, PlayCommand> = mapOf(),
    val playPotionEffect: Map<String, PlayPotionEffect> = mapOf(),
    val removePotionEffect: List<String> = listOf()
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
        val realColor: Color?
            get() = if (color != null) Color.fromRGB(Integer.decode(color?.replace("#", "0x"))) else null
    }

    @Suppress("PROVIDED_RUNTIME_TOO_LOW")
    @Serializable
    data class PlaySound(
        val name: String,
        val pitch: Float = 1f,
        val volume: Float = 1f,
        val cooldown:Int? = null
    ) {
        fun play(l: Location) {
            l.world.playSound(l, name, volume, pitch)
        }
    }

    @Suppress("PROVIDED_RUNTIME_TOO_LOW")
    @Serializable
    data class PlayCommand(
        val command: String,
        val asConsole: Boolean = false
    )

    @Suppress("PROVIDED_RUNTIME_TOO_LOW")
    @Serializable
    data class PlayPotionEffect(
        val name: String,
        val amplifier: Int = 1,
        val duration: Int = 200,
        val display: Boolean = true
    ) {
        fun play(e: LivingEntity?) {
            e ?: return
            val effect = PotionEffectType.getByName(name) ?: return
            e.addPotionEffect(PotionEffect(effect, duration, amplifier, display, display, display))
        }
    }
}