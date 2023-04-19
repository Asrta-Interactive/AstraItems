package com.atrainteractive.empire_items.models.enchants

import kotlinx.serialization.SerialName
@Suppress("PROVIDED_RUNTIME_TOO_LOW")
@kotlinx.serialization.Serializable
data class GenericEnchant(
    @SerialName("max_level")
    val minLevel: Int = 1,
    @SerialName("min_level")
    val maxLevel: Int = 5,
    @SerialName("exp_per_level")
    val expPerLevel:Int = 5,
    val chances: List<Double> = listOf(10.0, 10.0, 10.0),
    @SerialName("enchanting_table")
    val enchantingTable: Boolean = true,
    val anvil: Boolean = true
) {
    interface IGenericEnchant {
        val generic: GenericEnchant
    }
}
