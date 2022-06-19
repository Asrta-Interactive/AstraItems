package com.astrainteractive.empire_items.models

import com.astrainteractive.empire_items.api.EmpireItemsAPI.toAstraItem
import com.astrainteractive.empire_items.api.EmpireItemsAPI.toAstraItemOrItem
import kotlinx.serialization.Serializable
import org.bukkit.inventory.MerchantRecipe


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
        val middleItem: VillagerTradeItem?=null

    ) {

        fun toMerchantRecipe(): MerchantRecipe? {
            val result = id.toAstraItemOrItem(amount)?:return null
            val left = leftItem.id.toAstraItemOrItem(amount)?:return null
            val right = middleItem?.id?.toAstraItemOrItem(amount)
            return MerchantRecipe(result,0,Int.MAX_VALUE,false).apply {
                addIngredient(left)
                right?.let { addIngredient(it) }

            }
        }

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
