package com.astrainteractive.empire_items.api.models.recipies

import com.astrainteractive.empire_items.api.CraftingApi
import com.astrainteractive.empire_items.api.EmpireItemsAPI.toAstraItemOrItem
import kotlinx.serialization.SerialName


import kotlinx.serialization.Serializable

@Suppress("PROVIDED_RUNTIME_TOO_LOW")
@Serializable
data class Furnace(
    val id: String,
    val result: String = id,
    val returns: String? = null,
    val amount: Int = 1,
    val input: String,
    @SerialName("cook_time")
    val cookTime: Int = 20,
    val exp: Int = 20,
) {
    fun createRecipe() {
        val namespaceKey = CraftingApi.createKey(id)
        val resultItem = result.toAstraItemOrItem(amount) ?: return
        val inputItem = input.toAstraItemOrItem() ?: return
        val recipeChoice = CraftingApi.getRecipeChoice(inputItem)
        val furnaceRecipe = org.bukkit.inventory.FurnaceRecipe(
            namespaceKey,
            resultItem,
            recipeChoice,
            exp.toFloat(),
            cookTime
        )
        CraftingApi.addRecipe(id, result, furnaceRecipe)
    }
}
