package com.astrainteractive.empire_items.api.crafting.creators

import com.astrainteractive.empire_items.api.EmpireItemsAPI
import com.astrainteractive.empire_items.api.crafting.CraftingApi
import com.astrainteractive.empire_items.api.crafting.creators.core.RecipeCreator
import com.atrainteractive.empire_items.models.recipies.CraftingTable
import org.bukkit.Material
import org.bukkit.inventory.RecipeChoice
import org.bukkit.inventory.ShapedRecipe
import ru.astrainteractive.astralibs.di.IDependency
import ru.astrainteractive.astralibs.di.getValue

interface ICraftingTableRecipeCreator: RecipeCreator<CraftingTable>

class CraftingTableRecipeCreator(
    craftingApi: IDependency<CraftingApi>,
    empireItemsAPI: IDependency<EmpireItemsAPI>
):ICraftingTableRecipeCreator{
    private val craftingApi by craftingApi
    private val empireItemsAPI by empireItemsAPI

    override fun build(recipe: CraftingTable) {
        val pattern = recipe.pattern
        val namespaceKey = craftingApi.createKey(recipe.id)
        val shapedRecipe = ShapedRecipe(namespaceKey, empireItemsAPI.toAstraItemOrItemByID(recipe.result,recipe.amount) ?: return)
        if (pattern.size == 3)
            shapedRecipe.shape(pattern[0], pattern[1], pattern[2])
        else if (pattern.size == 2)
            shapedRecipe.shape(pattern[0], pattern[1])
        recipe.ingredients.forEach { (ch, item) ->
            val itemStack = empireItemsAPI.toAstraItemOrItemByID(item) ?: return@forEach
            val choice: RecipeChoice = craftingApi.getRecipeChoice(itemStack)
            if (ch.equals('x', ignoreCase = true))
                shapedRecipe.setIngredient(ch, RecipeChoice.MaterialChoice(Material.AIR))
            else
                shapedRecipe.setIngredient(ch, choice)
        }
        craftingApi.addRecipe(recipe.id, recipe.result, shapedRecipe)
    }

}