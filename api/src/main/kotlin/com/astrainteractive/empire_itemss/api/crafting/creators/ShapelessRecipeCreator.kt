package com.astrainteractive.empire_itemss.api.crafting.creators

import com.astrainteractive.empire_itemss.api.EmpireItemsAPI
import com.astrainteractive.empire_itemss.api.crafting.CraftingApi
import com.atrainteractive.empire_items.models.recipies.CraftingTable
import com.atrainteractive.empire_items.models.recipies.Furnace
import com.atrainteractive.empire_items.models.recipies.Shapeless
import org.bukkit.Material
import org.bukkit.inventory.RecipeChoice
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.ShapelessRecipe
import ru.astrainteractive.astralibs.di.IDependency
import ru.astrainteractive.astralibs.di.getValue

interface IShapelessRecipeCreator : IRecipeCreator<Shapeless>

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