package com.astrainteractive.empire_items.api.models.recipies

import com.astrainteractive.empire_items.api.CraftingApi
import com.astrainteractive.empire_items.api.EmpireItemsAPI.toAstraItemOrItem
import org.bukkit.Material
import org.bukkit.inventory.RecipeChoice
import org.bukkit.inventory.ShapedRecipe

import kotlinx.serialization.Serializable

@Suppress("PROVIDED_RUNTIME_TOO_LOW")
@Serializable
data class CraftingTable(
    val id: String,
    val result: String = id,
    val amount: Int = 1,
    val pattern: List<String>,
    val ingredients: Map<Char, String>,
) {
    fun createRecipe() {
        val namespaceKey = CraftingApi.createKey(id)
        val shapedRecipe = ShapedRecipe(namespaceKey, result.toAstraItemOrItem(amount) ?: return)
        if (pattern.size == 3)
            shapedRecipe.shape(pattern[0], pattern[1], pattern[2])
        else if (pattern.size == 2)
            shapedRecipe.shape(pattern[0], pattern[1])
        ingredients.forEach { (ch, item) ->
            val itemStack = item.toAstraItemOrItem() ?: return@forEach
            val choice: RecipeChoice = CraftingApi.getRecipeChoice(itemStack)
            if (ch.equals('x', ignoreCase = true))
                shapedRecipe.setIngredient(ch, RecipeChoice.MaterialChoice(Material.AIR))
            else
                shapedRecipe.setIngredient(ch, choice)
        }
        CraftingApi.addRecipe(id, result, shapedRecipe)
    }
}
