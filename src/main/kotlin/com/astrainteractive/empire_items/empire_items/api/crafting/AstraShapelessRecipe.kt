package com.astrainteractive.empire_items.empire_items.api.crafting

import com.astrainteractive.empire_items.empire_items.api.items.data.ItemManager.toAstraItemOrItem
import com.astrainteractive.astralibs.AstraYamlParser
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.inventory.ShapelessRecipe
import kotlin.math.max

data class AstraShapelessRecipe(
    val id: String,
    val input: String,
    val result: String,
    val amount: Int
) {
    fun createRecipe() {
        val namespaceKey = CraftingManager.createKey(id)
        val resultItem = result.toAstraItemOrItem(amount) ?: return
        val inputItem = input.toAstraItemOrItem() ?: return
        val recipeChoice = CraftingManager.getRecipeChoice(inputItem)
        val shapelessRecipe = ShapelessRecipe(namespaceKey, resultItem)
        shapelessRecipe.addIngredient(recipeChoice)
        CraftingManager.addRecipe(id, result, shapelessRecipe)
    }

    companion object {
        fun getAllRecipes(section: ConfigurationSection?) =
            section?.getKeys(false)?.mapNotNull {
                val s = section.getConfigurationSection(it) ?: return@mapNotNull null
                AstraShapelessRecipe(
                    id = s.getString("id") ?: s.name,
                    input = s.getString("input") ?: return@mapNotNull null,
                    result = s.getString("result") ?: return@mapNotNull null,
                    amount = s.getInt("amount", 1)
                )
            }
    }
}