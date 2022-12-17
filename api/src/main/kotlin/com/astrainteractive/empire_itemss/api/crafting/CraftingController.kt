package com.astrainteractive.empire_itemss.api.crafting

import com.astrainteractive.empire_itemss.api.EmpireItemsAPI
import com.astrainteractive.empire_itemss.api.crafting.creators.CraftingTableRecipeCreator
import com.astrainteractive.empire_itemss.api.crafting.creators.FurnaceRecipeCreator
import com.astrainteractive.empire_itemss.api.crafting.creators.ShapelessRecipeCreator
import com.astrainteractive.empire_itemss.api.utils.BukkitConstants
import org.bukkit.NamespacedKey
import org.bukkit.inventory.FurnaceRecipe
import org.bukkit.inventory.Recipe
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.ShapelessRecipe
import ru.astrainteractive.astralibs.AstraLibs
import ru.astrainteractive.astralibs.di.IDependency
import ru.astrainteractive.astralibs.di.getValue

class CraftingController(
    empireItemsAPI: IDependency<EmpireItemsAPI>,
    craftingTableRecipeCreator: IDependency<CraftingTableRecipeCreator>,
    furnaceRecipeCreator: IDependency<FurnaceRecipeCreator>,
    shapelessRecipeCreator: IDependency<ShapelessRecipeCreator>
) {
    private val empireItemsAPI by empireItemsAPI
    private val craftingTableRecipeCreator by craftingTableRecipeCreator
    private val furnaceRecipeCreator by furnaceRecipeCreator
    private val shapelessRecipeCreator by shapelessRecipeCreator
    private fun isCustomRecipe(key: NamespacedKey): Boolean =
        key.key.contains(BukkitConstants.ASTRA_CRAFTING)

    private fun isCustomRecipe(recipe: FurnaceRecipe) = isCustomRecipe(recipe.key)
    private fun isCustomRecipe(recipe: ShapedRecipe) = isCustomRecipe(recipe.key)
    private fun isCustomRecipe(recipe: ShapelessRecipe) = isCustomRecipe(recipe.key)

    private fun isCustomRecipe(recipe: Recipe): Boolean {
        return when (recipe) {
            is FurnaceRecipe -> isCustomRecipe(recipe)
            is ShapedRecipe -> isCustomRecipe(recipe)
            is ShapelessRecipe -> isCustomRecipe(recipe)
            else -> false
        }
    }

    fun create(){
        empireItemsAPI.craftingTableRecipeByID.values.forEach(craftingTableRecipeCreator::build)
        empireItemsAPI.furnaceRecipeByID.values.forEach(furnaceRecipeCreator::build)
        empireItemsAPI.shapelessRecipeByID.values.forEach(shapelessRecipeCreator::build)
    }

    fun clear() {
        val ite = AstraLibs.instance.server.recipeIterator()
        var recipe: Recipe?
        while (ite.hasNext()) {
            recipe = ite.next()
            if (isCustomRecipe(recipe)) {
                ite.remove()
                continue
            }
        }
    }
}