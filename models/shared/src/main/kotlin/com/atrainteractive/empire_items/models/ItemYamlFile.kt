package com.atrainteractive.empire_items.models

import com.atrainteractive.empire_items.models.mob.YmlMob
import com.atrainteractive.empire_items.models.recipies.CraftingTable
import com.atrainteractive.empire_items.models.recipies.Furnace
import com.atrainteractive.empire_items.models.recipies.Shapeless
import com.atrainteractive.empire_items.models.yml_item.YmlItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Suppress("PROVIDED_RUNTIME_TOO_LOW")
@Serializable
data class ItemYamlFile(
    val namespace: String = "empire_items",
    val crafting_table: Map<String, CraftingTable>? = null,
    val shapeless: Map<String, Shapeless>? = null,
    val furnace: Map<String, Furnace>? = null,
    val loot: Map<String, Loot>? = null,
    val villagerTrades: Map<String, VillagerTradeInfo>? = null,
    val fontImages: Map<String, FontImage> = mapOf(),
    val merchant_recipes:Map<String, YamlMerchantRecipe>? = null,
    val yml_items: Map<String, YmlItem>? = null,
    @SerialName("sounds")
    val ymlSounds: Map<String, YmlSound> = mapOf(),
    @SerialName("mobs")
    val ymlMob: Map<String, YmlMob> = mapOf()
)
