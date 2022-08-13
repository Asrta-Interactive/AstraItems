package com.astrainteractive.empire_items.api.models.mob

import com.astrainteractive.empire_items.api.models.yml_item.Interact
import org.bukkit.boss.BarStyle
import kotlin.random.Random

import kotlinx.serialization.Serializable

@Suppress("PROVIDED_RUNTIME_TOO_LOW")
@Serializable
data class YmlMob(
    val id: String,
    val entity: String,
    val modelID: String,
    val decreaseDamageByRange: Boolean = true,
    val canBurn: Boolean = true,
    val idleSound: List<String> = listOf(),
    val potionEffects: Map<String, Interact.PlayPotionEffect> = mapOf(),
    val attributes: Map<String, RandomAttribute> = emptyMap(),
    val hitDelay: Int = 0,
    val hitRange: Double,
    val spawn: Map<String, SpawnInfo>? = null,
    val bossBar: YmlMobBossBar? = null,
    val ignoreMobs: List<String> = emptyList(),
    val events: Map<String, YmlMobEvent> = mapOf(),
) {


    @Suppress("PROVIDED_RUNTIME_TOO_LOW")
    @Serializable
    data class YmlMobEvent(
        val id: String,
        val cooldown: Int? = null,
        val eventName: String = id,
        val playSound: Interact.PlaySound? = null,
        val boneParticle: Map<String, BoneParticle> = emptyMap(),
        val playPotionEffect: Map<String, Interact.PlayPotionEffect> = emptyMap(),
        val actions: Map<String, MobAction> = emptyMap(),
    ) {
        @Suppress("PROVIDED_RUNTIME_TOO_LOW")
        @Serializable
        data class BoneParticle(
            val bones: List<String>,
            val cooldown: Int?,
            val particle: Interact.PlayParticle,
        )

        @Suppress("PROVIDED_RUNTIME_TOO_LOW")
        @Serializable
        data class MobAction(
            val id: String,
            val startAfter: Int,
            val condition: Condition? = null,
            val summonProjectile: Map<String, SummonProjectile> = emptyMap(),
            val summonMinions: Map<String, SummonMinion> = emptyMap(),
        ) {
            @Suppress("PROVIDED_RUNTIME_TOO_LOW")
            @Serializable
            data class SummonProjectile(
                val damage: Int,
                val playParticle: Interact.PlayParticle,
            )

            @Suppress("PROVIDED_RUNTIME_TOO_LOW")
            @Serializable
            data class SummonMinion(
                val type: String,
                val amount: Int,
                val attributes: Map<String, RandomAttribute> = mapOf(),
                val potionEffects: Map<String, Interact.PlayPotionEffect> = mapOf(),
            )

            @Suppress("PROVIDED_RUNTIME_TOO_LOW")
            @Serializable
            data class Condition(
                val whenHPBelow: Int? = null,
                val animationNames: List<String>? = null,
                val cooldown: Int? = null,
                val chance: Double? = null,
            )
        }

    }

    @Suppress("PROVIDED_RUNTIME_TOO_LOW")
    @Serializable
    data class YmlMobBossBar(
        val name: String,
        val color: String,
        val barStyle: String = BarStyle.SOLID.name,
        val flags: List<String> = listOf(),
    )

    @Suppress("PROVIDED_RUNTIME_TOO_LOW")
    @Serializable
    data class SpawnInfo(
        val biomes: List<String> = listOf(),
        val invertBiomes: Boolean = false,
        val minY: Int = -100,
        val maxY: Int = 512,
        val replace: Map<String, Double>,
    )

    @Suppress("PROVIDED_RUNTIME_TOO_LOW")
    @Serializable
    data class RandomAttribute(
        val value: Double? = null,
        val minValue: Double? = null,
        val maxValue: Double? = null,
        val name: String,
    ) {
        val realValue: Double
            get() = value ?: Random.nextDouble(minValue!!, maxValue!! + 1)
    }
}