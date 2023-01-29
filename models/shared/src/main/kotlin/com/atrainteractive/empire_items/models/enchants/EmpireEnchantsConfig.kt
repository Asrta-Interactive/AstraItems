package com.atrainteractive.empire_items.models.enchants

import kotlinx.serialization.SerialName

@Suppress("PROVIDED_RUNTIME_TOO_LOW")
@kotlinx.serialization.Serializable
data class EmpireEnchantsConfig(
    val enabled: Boolean = false,
    @SerialName("potion_enchants")
    val potionEnchants: Map<String, PotionEnchant> = emptyMap(),
    val enchants: EmpireEnchants,
) {
    @Suppress("PROVIDED_RUNTIME_TOO_LOW")
    @kotlinx.serialization.Serializable
    data class PotionEnchant(
        val id: String,
        val effect: String,
        @SerialName("item_types")
        val itemTypes: List<EnchantItemType> = emptyList(),
        val items: List<String> = emptyList(),
        override val generic: GenericEnchant,
        val value: Double,
    ) : GenericEnchant.IGenericEnchant {
        @Suppress("PROVIDED_RUNTIME_TOO_LOW")
        @kotlinx.serialization.Serializable
        enum class EnchantItemType {
            ARMOR, SWORDS, AXES, PICKAXES;
        }
    }

    @Suppress("PROVIDED_RUNTIME_TOO_LOW")
    @kotlinx.serialization.Serializable
    data class EmpireEnchants(
        val VAMPIRISM: GenericValueEnchant,
        val FROST_ASPECT: GenericValueEnchant,
        val LAVA_WALKER_ENCHANT: GenericValueEnchant,
        val BUTCHER: GenericValueEnchant,
        val VYDERLIGHT: GenericValueEnchant,
        val AQUALIGHT: GenericValueEnchant,
        val ANTI_FALL: GenericValueEnchant,
        val MEGA_JUMP: GenericValueEnchant,
        val MOB_ARENA_ENCHANT: SpawnMobArenaEnchant,
    )
}