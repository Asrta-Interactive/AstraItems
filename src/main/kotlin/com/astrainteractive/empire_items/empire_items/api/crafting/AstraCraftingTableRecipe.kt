package com.astrainteractive.empire_items.empire_items.api.crafting

import com.astrainteractive.empire_items.empire_items.api.items.data.ItemManager.toAstraItemOrItem
import com.astrainteractive.astralibs.AstraYamlParser
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.inventory.RecipeChoice
import org.bukkit.inventory.ShapedRecipe
import kotlin.math.max

data class AstraCraftingTableRecipe(
    val id: String,
    val result: String,
    val amount: Int,
    val pattern: List<String>?,
    val ingredients: Map<Char, String>?
) {
    fun createRecipe() {
        val namespaceKey = CraftingManager.createKey(id)
        val shapedRecipe = ShapedRecipe(namespaceKey, result.toAstraItemOrItem(amount) ?: return)
        pattern ?: return
        if (pattern.size == 3)
            shapedRecipe.shape(pattern[0], pattern[1], pattern[2])
        else if (pattern.size == 2)
            shapedRecipe.shape(pattern[0], pattern[1])
        ingredients?.forEach { (ch, item) ->
            val itemStack = item.toAstraItemOrItem() ?: return@forEach
            val choice: RecipeChoice = CraftingManager.getRecipeChoice(itemStack)
            if (ch.equals('x', ignoreCase = true))
                shapedRecipe.setIngredient(ch, RecipeChoice.MaterialChoice(Material.AIR))
            else
                shapedRecipe.setIngredient(ch, choice)
        }
        CraftingManager.addRecipe(id, result, shapedRecipe)
    }


    companion object {
        fun getAllRecipes(s: ConfigurationSection?) =
            s?.getKeys(false)?.mapNotNull {
                getRecipe(s.getConfigurationSection(it))
            }

        private fun getRecipe(s: ConfigurationSection?): AstraCraftingTableRecipe? {
            val parser = AstraYamlParser.parser
            val res = parser.configurationSectionToClass<AstraCraftingTableRecipe>(s ?: return null) ?: return null
            val id = parser.fixNull(res.id, s.name)
            val recipe = AstraCraftingTableRecipe(
                id = id,
                result = parser.fixNull(res.result, parser.fixNull(res.id, s.name)),
                pattern = res.pattern,
                amount = max(res.amount, 1),
                ingredients = res.ingredients
            )
            return recipe
        }
    }
}


