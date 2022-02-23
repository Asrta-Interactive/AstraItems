package com.astrainteractive.empire_items.api.crafting

import com.astrainteractive.empire_items.api.items.data.ItemApi.toAstraItemOrItem
import com.astrainteractive.empire_items.api.mobs.data.getMap
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.inventory.RecipeChoice
import org.bukkit.inventory.ShapedRecipe

data class AstraCraftingTableRecipe(
    val id: String,
    val result: String,
    val amount: Int,
    val pattern: List<String>?,
    val ingredients: Map<Char, String>?
) {
    fun createRecipe() {
        val namespaceKey = CraftingApi.createKey(id)
        val shapedRecipe = ShapedRecipe(namespaceKey, result.toAstraItemOrItem(amount) ?: return)
        pattern ?: return
        if (pattern.size == 3)
            shapedRecipe.shape(pattern[0], pattern[1], pattern[2])
        else if (pattern.size == 2)
            shapedRecipe.shape(pattern[0], pattern[1])
        ingredients?.forEach { (ch, item) ->
            val itemStack = item.toAstraItemOrItem() ?: return@forEach
            val choice: RecipeChoice = CraftingApi.getRecipeChoice(itemStack)
            if (ch.equals('x', ignoreCase = true))
                shapedRecipe.setIngredient(ch, RecipeChoice.MaterialChoice(Material.AIR))
            else
                shapedRecipe.setIngredient(ch, choice)
        }
        CraftingApi.addRecipe(id, result, shapedRecipe)
    }


    companion object {
        fun getAllRecipes(section: ConfigurationSection?) =
            section?.getKeys(false)?.mapNotNull {craftId->
                val s = section.getConfigurationSection(craftId) ?: return@mapNotNull null
                val id = s.getString("id") ?: s.name
                AstraCraftingTableRecipe(
                    id =id,
                    result = s.getString("result")?:id,
                    amount = s.getInt("amount", 1),
                    pattern = s.getStringList("pattern"),
                    ingredients = s.getMap<Char, String>("ingredients")
                )
            }
    }
}


