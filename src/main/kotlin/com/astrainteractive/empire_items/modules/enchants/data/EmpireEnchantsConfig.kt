package com.astrainteractive.empire_items.modules.enchants.data

import com.astrainteractive.empire_items.empire_items.util.EmpireSerializer
import com.astrainteractive.empire_items.empire_items.util.Files
import com.astrainteractive.empire_items.models._GuiConfig
import com.astrainteractive.empire_items.modules.enchants.api.EmpireEnchantApi
import com.astrainteractive.empire_items.modules.enchants.data.enchants.GenericValueEnchant
import com.astrainteractive.empire_items.modules.enchants.data.enchants.SpawnMobArenaEnchant
import kotlinx.serialization.SerialName
import org.bukkit.Material

val EmpireEnchantsConfig: _EmpireEnchantsConfig
    get() = _EmpireEnchantsConfig.instance

@Suppress("PROVIDED_RUNTIME_TOO_LOW")
@kotlinx.serialization.Serializable
data class _EmpireEnchantsConfig(
    val enabled: Boolean = false,
    @SerialName("potion_enchants")
    val potionEnchants: Map<String, PotionEnchant> = emptyMap(),
    val enchants: EmpireEnchants
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
        val value: Double
    ) : GenericEnchant.IGenericEnchant {
        @Suppress("PROVIDED_RUNTIME_TOO_LOW")
        @kotlinx.serialization.Serializable
        enum class EnchantItemType {
            ARMOR, SWORDS, AXES, PICKAXES;

            val getList: List<Material>
                get() {
                    return when (this) {
                        ARMOR -> EmpireEnchantApi.armorItems
                        SWORDS -> EmpireEnchantApi.swords
                        AXES -> EmpireEnchantApi.axes
                        PICKAXES -> EmpireEnchantApi.pickaxes
                    }
                }
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
        val MOB_ARENA_ENCHANT: SpawnMobArenaEnchant
    )

    companion object {
        lateinit var instance: _EmpireEnchantsConfig
            private set

        fun create(): _EmpireEnchantsConfig {
            val _config = EmpireSerializer.toClass<_EmpireEnchantsConfig>(Files.enchantsModule.getFile())
            instance = _config!!
            return instance
        }
    }
}