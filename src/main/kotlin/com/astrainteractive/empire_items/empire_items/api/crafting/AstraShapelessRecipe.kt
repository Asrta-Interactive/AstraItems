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
        fun getAllRecipes(s: ConfigurationSection?) =
            s?.getKeys(false)?.mapNotNull {
                getRecipe(s.getConfigurationSection(it))
            }

        private fun getRecipe(s: ConfigurationSection?): AstraShapelessRecipe? {
            val parser = AstraYamlParser.parser
            val res = parser.configurationSectionToClass<AstraShapelessRecipe>(s ?: return null) ?: return null
            val id = parser.fixNull(res.id, s.name)
            return AstraShapelessRecipe(id = id, input = res.input, result = res.result, amount = max(1, res.amount))
        }
    }
}