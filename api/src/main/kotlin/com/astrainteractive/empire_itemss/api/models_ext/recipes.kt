package com.astrainteractive.empire_itemss.api.models_ext

import com.astrainteractive.empire_itemss.api.CraftingApi
import com.astrainteractive.empire_itemss.api.EmpireItemsAPI.toAstraItemOrItem
import com.atrainteractive.empire_items.models.recipies.CraftingTable
import com.atrainteractive.empire_items.models.recipies.Furnace
import com.atrainteractive.empire_items.models.recipies.Shapeless
import org.bukkit.Material
import org.bukkit.inventory.RecipeChoice
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.ShapelessRecipe

fun CraftingTable.createRecipe() {
    val namespaceKey = CraftingApi.createKey(id)
    val shapedRecipe = ShapedRecipe(namespaceKey, result.toAstraItemOrItem(amount) ?: return)
    if (pattern.size == 3)
        shapedRecipe.shape(pattern[0], pattern[1], pattern[2])
    else if (pattern.size == 2)
        shapedRecipe.shape(pattern[0], pattern[1])
    ingredients.forEach { (ch, item) ->
        val itemStack = item.toAstraItemOrItem() ?: return@forEach
        val choice: RecipeChoice = CraftingApi.getRecipeChoice(itemStack)
        if (ch.equals('x', ignoreCase = true))
            shapedRecipe.setIngredient(ch, RecipeChoice.MaterialChoice(Material.AIR))
        else
            shapedRecipe.setIngredient(ch, choice)
    }
    CraftingApi.addRecipe(id, result, shapedRecipe)
}
fun Furnace.createRecipe() {
    val namespaceKey = CraftingApi.createKey(id)
    val resultItem = result.toAstraItemOrItem(amount) ?: return
    val inputItem = input.toAstraItemOrItem() ?: return
    val recipeChoice = CraftingApi.getRecipeChoice(inputItem)
    val furnaceRecipe = org.bukkit.inventory.FurnaceRecipe(
        namespaceKey,
        resultItem,
        recipeChoice,
        exp.toFloat(),
        cookTime
    )
    CraftingApi.addRecipe(id, result, furnaceRecipe)
}
fun Shapeless.createRecipe() {
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