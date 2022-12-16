package com.astrainteractive.empire_itemss.api.models_ext

import com.astrainteractive.empire_itemss.api.CraftingApi
import com.astrainteractive.empire_itemss.api.EmpireItemsAPI
import com.atrainteractive.empire_items.models.recipies.CraftingTable
import com.atrainteractive.empire_items.models.recipies.Furnace
import com.atrainteractive.empire_items.models.recipies.Shapeless
import org.bukkit.Material
import org.bukkit.inventory.RecipeChoice
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.ShapelessRecipe
import ru.astrainteractive.astralibs.di.IDependency

private val craftingApi: CraftingApi by IDependency
private val empireItemsAPI: EmpireItemsAPI by IDependency
fun CraftingTable.createRecipe() {

    val namespaceKey = craftingApi.createKey(id)
    val shapedRecipe = ShapedRecipe(namespaceKey, empireItemsAPI.toAstraItemOrItemByID(result,amount) ?: return)
    if (pattern.size == 3)
        shapedRecipe.shape(pattern[0], pattern[1], pattern[2])
    else if (pattern.size == 2)
        shapedRecipe.shape(pattern[0], pattern[1])
    ingredients.forEach { (ch, item) ->
        val itemStack = empireItemsAPI.toAstraItemOrItemByID(item) ?: return@forEach
        val choice: RecipeChoice = craftingApi.getRecipeChoice(itemStack)
        if (ch.equals('x', ignoreCase = true))
            shapedRecipe.setIngredient(ch, RecipeChoice.MaterialChoice(Material.AIR))
        else
            shapedRecipe.setIngredient(ch, choice)
    }
    craftingApi.addRecipe(id, result, shapedRecipe)
}
fun Furnace.createRecipe() {
    val namespaceKey = craftingApi.createKey(id)
    val resultItem = empireItemsAPI.toAstraItemOrItemByID(result,amount) ?: return
    val inputItem = empireItemsAPI.toAstraItemOrItemByID(input) ?: return
    val recipeChoice = craftingApi.getRecipeChoice(inputItem)
    val furnaceRecipe = org.bukkit.inventory.FurnaceRecipe(
        namespaceKey,
        resultItem,
        recipeChoice,
        exp.toFloat(),
        cookTime
    )
    craftingApi.addRecipe(id, result, furnaceRecipe)
}
fun Shapeless.createRecipe() {
    val namespaceKey = craftingApi.createKey(id)
    val resultItem = empireItemsAPI.toAstraItemOrItemByID(result,amount) ?: return
    val inputItem = empireItemsAPI.toAstraItemOrItemByID(input)
    val shapelessRecipe = ShapelessRecipe(namespaceKey, resultItem)
    inputItem?.let { craftingApi.getRecipeChoice(it) }?.let { shapelessRecipe.addIngredient(it) }
    inputs.forEach { it ->
        val rc = craftingApi.getRecipeChoice(empireItemsAPI.toAstraItemOrItemByID(it) ?: return@forEach)
        shapelessRecipe.addIngredient(rc)
    }
    craftingApi.addRecipe(id, result, shapelessRecipe)
}