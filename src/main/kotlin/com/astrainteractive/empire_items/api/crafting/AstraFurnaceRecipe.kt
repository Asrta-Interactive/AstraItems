package com.astrainteractive.empire_items.api.crafting

import com.astrainteractive.empire_items.api.items.data.ItemApi.toAstraItemOrItem
import org.bukkit.configuration.ConfigurationSection

data class AstraFurnaceRecipe(
    val id: String,
    val returns: String?,
    val result: String,
    val input: String,
    val cookTime: Int,
    val exp: Int,
    val amount: Int
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

    companion object {
        fun getAllRecipes(section: ConfigurationSection?) =
            section?.getKeys(false)?.mapNotNull {
                val s = section.getConfigurationSection(it) ?: return@mapNotNull null
                AstraFurnaceRecipe(
                    id = s.getString("id") ?: s.name,
                    input = s.getString("input") ?: return null,
                    returns = s.getString("returns"),
                    result = s.getString("result") ?: return null,
                    amount = s.getInt("amount", 1),
                    cookTime = s.getInt("cookTime", 200),
                    exp = s.getInt("exp")
                )
            }

    }
}