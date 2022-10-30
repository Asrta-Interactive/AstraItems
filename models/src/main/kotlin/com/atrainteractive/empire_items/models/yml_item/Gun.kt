package com.atrainteractive.empire_items.models.yml_item
import kotlinx.serialization.Serializable

@Suppress("PROVIDED_RUNTIME_TOO_LOW")
@Serializable
data class Gun(
    val cooldown: Int? = null,
    val recoil: Double? = null,
    val clipSize: Int? = null,
    val bulletWeight: Double = 1.0,
    val bulletTrace: Int = 100,
    val color: String? = null,
    val damage: Double? = null,
    val reload: String? = null,
    val particle: String? = null,
    val noAmmoSound: String = "",
    val reloadSound: String = "",
    val fullSound: String = "",
    val shootSound: String = "",
    val radius: Double = 1.0,
    val radiusSneak: Double = 4.0,
    val explosion: Int? = null,
    val advanced: Advanced? = null,
) {
    @Suppress("PROVIDED_RUNTIME_TOO_LOW")
    @Serializable
    data class Advanced(
        val armorPenetration: Map<String, Double> = mapOf(),
        val onHit: OnHit? = null,
    ) {
        @Suppress("PROVIDED_RUNTIME_TOO_LOW")
        @Serializable
        data class OnHit(
            val fireTicks: Int? = null,
            val freezeTicks: Int? = null,
            val ignorePlayer:Boolean = false,
            val ignite: Int? = null,
            val playPotionEffect: Map<String, Interact.PlayPotionEffect> = mapOf(),
        )
    }

}