package com.astrainteractive.empire_itemss.api.crafting.creators

import com.astrainteractive.empire_itemss.api.EmpireItemsAPI
import com.astrainteractive.empire_itemss.api.crafting.CraftingApi
import com.atrainteractive.empire_items.models.recipies.CraftingTable
import com.atrainteractive.empire_items.models.recipies.Furnace
import org.bukkit.Material
import org.bukkit.inventory.RecipeChoice
import org.bukkit.inventory.ShapedRecipe
import ru.astrainteractive.astralibs.di.IDependency
import ru.astrainteractive.astralibs.di.getValue

interface IFurnaceRecipeCreator : IRecipeCreator<Furnace>

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