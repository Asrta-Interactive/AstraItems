package com.astrainteractive.empire_items.empire_items.api.crafting

import com.astrainteractive.empire_items.empire_items.api.items.data.ItemManager.toAstraItemOrItem
import com.astrainteractive.astralibs.AstraYamlParser
import org.bukkit.configuration.ConfigurationSection
import kotlin.math.max

data class AstraFurnaceRecipe(
    val id: String,
    val result: String,
    val input: String,
    val cookTime: Int,
    val exp: Int,
    val amount: Int
) {


    fun createRecipe() {
        val namespaceKey = CraftingManager.createKey(id)
        val resultItem = result.toAstraItemOrItem(amount) ?: return
        val inputItem = input.toAstraItemOrItem() ?: return
        val recipeChoice = CraftingManager.getRecipeChoice(inputItem)
        val furnaceRecipe = org.bukkit.inventory.FurnaceRecipe(
            namespaceKey,
            resultItem,
            recipeChoice,
            exp.toFloat(),
            cookTime
        )
        CraftingManager.addRecipe(id, result, furnaceRecipe)
    }

    companion object {
        fun getAllRecipes(s: ConfigurationSection?) =
            s?.getKeys(false)?.mapNotNull {
                getRecipe(s.getConfigurationSection(it))
            }

        private fun getRecipe(s: ConfigurationSection?): AstraFurnaceRecipe? {
            val parser = AstraYamlParser.parser
            val res = parser.configurationSectionToClass<AstraFurnaceRecipe>(s ?: return null) ?: return null
            val id = parser.fixNull(res.id, s.name)
            return AstraFurnaceRecipe(
                id = id,
                input = res.input,
                result = res.result,
                cookTime = max(20, res.cookTime),
                exp = max(1, res.exp),
                amount = max(res.amount, 1)
            )
        }
    }
}