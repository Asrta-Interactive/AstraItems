package com.atrainteractive.empire_items.models

import kotlinx.serialization.Serializable

@Suppress("PROVIDED_RUNTIME_TOO_LOW")
@Serializable
data class YamlMerchantRecipe(
    val id:String,
    val title:String,
    val recipes:Map<String, VillagerTradeInfo.VillagerTrade>
)