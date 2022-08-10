package com.astrainteractive.empire_items.api.enchants.models

import com.astrainteractive.empire_items.api.models.yml_item.Interact

@Suppress("PROVIDED_RUNTIME_TOO_LOW")
@kotlinx.serialization.Serializable
data class GenericValueEnchant(
    override val generic: GenericEnchant,
    val value: Double
) : GenericEnchant.IGenericEnchant

@Suppress("PROVIDED_RUNTIME_TOO_LOW")
@kotlinx.serialization.Serializable
data class SpawnMobArenaEnchant(
    override val generic: GenericEnchant,
    val playCommand: Map<String, Interact.PlayCommand>,
    val playSound: Interact.PlaySound,
    val playParticle: Interact.PlayParticle,
) : GenericEnchant.IGenericEnchant