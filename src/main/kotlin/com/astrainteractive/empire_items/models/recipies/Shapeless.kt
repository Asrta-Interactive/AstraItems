package com.astrainteractive.empire_items.models.recipies

import com.astrainteractive.empire_items.api.CraftingApi
import com.astrainteractive.empire_items.api.EmpireItemsAPI.toAstraItemOrItem
import kotlinx.serialization.Serializable
import org.bukkit.inventory.ShapelessRecipe

@Suppress("PROVIDED_RUNTIME_TOO_LOW")
@Serializable
data class Shapeless(
    val id: String,
    val result: String = id,
    val amount: Int = 1,
    val input: String? = null,
    val inputs: List<String> = listOf()
) {
    fun createRecipe() {
        val namespaceKey = CraftingApi.createKey(id)
        val resultItem = result.toAstraItemOrItem(amount) ?: return
        val inputItem = input.toAstraItemOrItem()
        val shapelessRecipe = ShapelessRecipe(namespaceKey, resultItem)
        inputItem?.let { CraftingApi.getRecipeChoice(it) }?.let { shapelessRecipe.addIngredient(it) }
        inputs.forEach { it ->
            val rc = CraftingApi.getRecipeChoice(it.toAstraItemOrItem() ?: return@forEach)
            shapelessRecipe.addIngredient(rc)
        }
        CraftingApi.addRecipe(id, result, shapelessRecipe)
    }
}