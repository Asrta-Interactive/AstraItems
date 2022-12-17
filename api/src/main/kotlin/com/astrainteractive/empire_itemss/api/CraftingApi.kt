package com.astrainteractive.empire_itemss.api

import ru.astrainteractive.astralibs.AstraLibs
import ru.astrainteractive.astralibs.Logger
import com.astrainteractive.empire_itemss.api.models_ext.createRecipe
import com.astrainteractive.empire_itemss.api.utils.BukkitConstants
import com.astrainteractive.empire_itemss.api.utils.IManager
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.inventory.*
import ru.astrainteractive.astralibs.di.IReloadable
import ru.astrainteractive.astralibs.di.getValue

class CraftingApi(
    empireItemsApi: IReloadable<EmpireItemsAPI>
) : IManager {
    protected val empireItemsApi by empireItemsApi
    private object CraftingManager {
        private fun isCustomRecipe(key: NamespacedKey): Boolean =
            key.key.contains(BukkitConstants.ASTRA_CRAFTING)

        private fun isCustomRecipe(recipe: FurnaceRecipe) = CraftingManager.isCustomRecipe(recipe.key)
        private fun isCustomRecipe(recipe: ShapedRecipe) = CraftingManager.isCustomRecipe(recipe.key)
        private fun isCustomRecipe(recipe: ShapelessRecipe) = CraftingManager.isCustomRecipe(recipe.key)

        private fun isCustomRecipe(recipe: Recipe): Boolean {
            return when (recipe) {
                is FurnaceRecipe -> isCustomRecipe(recipe)
                is ShapedRecipe -> isCustomRecipe(recipe)
                is ShapelessRecipe -> isCustomRecipe(recipe)
                else -> false
            }
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

    private val keyMap = mutableMapOf<String, MutableList<NamespacedKey>>()
    val recipesMap: MutableMap<String, List<Recipe>> = mutableMapOf()

    private fun addToMap(id: String, key: NamespacedKey) {
        if (!keyMap.containsKey(id))
            keyMap[id] = mutableListOf()
        val list = keyMap[id]!!
        list.add(key)
        keyMap[id] = list
    }

    fun getFurnaceByInputId(id: String) = empireItemsApi.furnaceRecipeByID.values.firstOrNull { it.input == id }
    fun getKeysById(id: String) = keyMap[id]

    override fun onEnable() {
        empireItemsApi.craftingTableRecipeByID.values.forEach {
            it.createRecipe()
        }
        empireItemsApi.furnaceRecipeByID.values.forEach {
            it.createRecipe()
        }
        empireItemsApi.shapelessRecipeByID.values.forEach {
            it.createRecipe()
        }
    }

    override fun onDisable() {
        CraftingManager.clear()
    }

    fun createKey(id: String): NamespacedKey {
        val key = NamespacedKey(AstraLibs.instance, BukkitConstants.ASTRA_CRAFTING + id)
        addToMap(id, key)
        return key
    }

    fun getRecipeChoice(inputItem: ItemStack) = if (inputItem.empireID != null)
        RecipeChoice.ExactChoice(inputItem)
    else RecipeChoice.MaterialChoice(inputItem.type)

    fun addRecipe(id: String, result: String, recipe: Recipe) = try {
        Bukkit.addRecipe(recipe)
        recipesMap[result] = mutableListOf<Recipe>().apply {
            addAll(recipesMap[result]?: listOf())
            add(recipe)
        }
    } catch (e: IllegalStateException) {
        Logger.warn(
            "Не удалось добавить крафт id:${id} result:${result}. ID не должны повтаряться! ${e.message}",
            "Crafting"
        )
    }

    fun usedInCraft(id: String): MutableSet<String> {
        val craftingTable = empireItemsApi.craftingTableRecipeByID.values.filter {
            it.ingredients.values.contains(id)
        }.map { it.result }
        val furnace = empireItemsApi.furnaceRecipeByID.values.filter {
            it.input == id
        }.map { it.result }
        val shapeless = empireItemsApi.shapelessRecipeByID.values.filter {
            it.input == id || it.inputs.contains(id)
        }.map { it.result }
        return mutableSetOf<String>().apply {
            addAll(craftingTable)
            addAll(furnace)
            addAll(shapeless)
        }
    }
}