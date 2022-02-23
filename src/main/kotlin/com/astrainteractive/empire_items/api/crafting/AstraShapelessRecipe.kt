package com.astrainteractive.empire_items.api.crafting

import com.astrainteractive.empire_items.api.items.data.ItemApi.toAstraItemOrItem
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.inventory.ShapelessRecipe

data class AstraShapelessRecipe(
    val id: String,
    val input: String?,
    val inputs: List<String>,
    val result: String,
    val amount: Int
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

    companion object {
        fun getAllRecipes(section: ConfigurationSection?) =
            section?.getKeys(false)?.mapNotNull {
                val s = section.getConfigurationSection(it) ?: return@mapNotNull null
                val r = AstraShapelessRecipe(
                    id = s.getString("id") ?: s.name,
                    input = s.getString("input"),
                    inputs = s.getStringList("inputs"),
                    result = s.getString("result") ?: return@mapNotNull null,
                    amount = s.getInt("amount", 1)
                )
                return@mapNotNull r
            }
    }
}