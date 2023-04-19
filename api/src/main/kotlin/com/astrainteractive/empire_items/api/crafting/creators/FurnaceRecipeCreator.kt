package com.astrainteractive.empire_items.api.crafting.creators

import com.astrainteractive.empire_items.api.EmpireItemsAPI
import com.astrainteractive.empire_items.api.crafting.CraftingApi
import com.astrainteractive.empire_items.api.crafting.creators.core.RecipeCreator
import com.atrainteractive.empire_items.models.recipies.Furnace
import ru.astrainteractive.astralibs.di.IDependency
import ru.astrainteractive.astralibs.di.getValue

interface IFurnaceRecipeCreator : RecipeCreator<Furnace>

class FurnaceRecipeCreator(
    craftingApi: IDependency<CraftingApi>,
    empireItemsAPI: IDependency<EmpireItemsAPI>
) : IFurnaceRecipeCreator {
    private val craftingApi by craftingApi
    private val empireItemsAPI by empireItemsAPI

    override fun build(recipe: Furnace,) {
        val namespaceKey = craftingApi.createKey(recipe.id)
        val resultItem = empireItemsAPI.toAstraItemOrItemByID(recipe.result, recipe.amount) ?: return
        val inputItem = empireItemsAPI.toAstraItemOrItemByID(recipe.input) ?: return
        val recipeChoice = craftingApi.getRecipeChoice(inputItem)
        val furnaceRecipe = org.bukkit.inventory.FurnaceRecipe(
            namespaceKey,
            resultItem,
            recipeChoice,
            recipe.exp.toFloat(),
            recipe.cookTime
        )
        craftingApi.addRecipe(recipe.id, recipe.result, furnaceRecipe)
    }

}