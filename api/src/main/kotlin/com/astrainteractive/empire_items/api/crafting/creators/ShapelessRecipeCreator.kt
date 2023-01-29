package com.astrainteractive.empire_items.api.crafting.creators

import com.astrainteractive.empire_items.api.EmpireItemsAPI
import com.astrainteractive.empire_items.api.crafting.CraftingApi
import com.astrainteractive.empire_items.api.crafting.creators.core.RecipeCreator
import com.atrainteractive.empire_items.models.recipies.Shapeless
import org.bukkit.inventory.ShapelessRecipe
import ru.astrainteractive.astralibs.di.IDependency
import ru.astrainteractive.astralibs.di.getValue

interface IShapelessRecipeCreator : RecipeCreator<Shapeless>

class ShapelessRecipeCreator(
    craftingApi: IDependency<CraftingApi>,
    empireItemsAPI: IDependency<EmpireItemsAPI>
) : IShapelessRecipeCreator {
    private val craftingApi by craftingApi
    private val empireItemsAPI by empireItemsAPI

    override fun build(recipe: Shapeless) {
        val namespaceKey = craftingApi.createKey(recipe.id)
        val resultItem = empireItemsAPI.toAstraItemOrItemByID(recipe.result,recipe.amount) ?: return
        val inputItem = empireItemsAPI.toAstraItemOrItemByID(recipe.input)
        val shapelessRecipe = ShapelessRecipe(namespaceKey, resultItem)
        inputItem?.let { craftingApi.getRecipeChoice(it) }?.let { shapelessRecipe.addIngredient(it) }
        recipe.inputs.forEach { it ->
            val rc = craftingApi.getRecipeChoice(empireItemsAPI.toAstraItemOrItemByID(it) ?: return@forEach)
            shapelessRecipe.addIngredient(rc)
        }
        craftingApi.addRecipe(recipe.id, recipe.result, shapelessRecipe)
    }

}