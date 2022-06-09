package com.astrainteractive.empire_items.models

import kotlinx.serialization.Serializable


@Suppress("PROVIDED_RUNTIME_TOO_LOW")
@Serializable
data class VillagerTradeInfo(
    val id: String,
    val profession: String,
    val trades: Map<String, VillagerTrade>
) {
    @Suppress("PROVIDED_RUNTIME_TOO_LOW")
    @Serializable
    data class VillagerTrade(
        val id: String,
        val chance: Double = 100.0,
        val amount: Int = 1,
        val minUses: Int = 10,
        val maxUses: Int = 20,
        val minLevel: Int = 1,
        val maxLevel: Int = 5,
        val leftItem: VillagerTradeItem,
        val middleItem: VillagerTradeItem

    ) {
        @Suppress("PROVIDED_RUNTIME_TOO_LOW")
        @Serializable
        data class VillagerTradeItem(
            val id: String,
            val minAmount: Int = 1,
            val maxAmount: Int = 5,
            val amount: Int,
        )
    }
}
