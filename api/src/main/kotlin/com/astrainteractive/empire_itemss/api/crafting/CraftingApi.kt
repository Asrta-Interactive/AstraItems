package com.astrainteractive.empire_itemss.api.crafting

import com.astrainteractive.empire_itemss.api.EmpireItemsAPI
import com.astrainteractive.empire_itemss.api.empireID
import com.astrainteractive.empire_itemss.api.utils.BukkitConstants
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe
import org.bukkit.inventory.RecipeChoice
import ru.astrainteractive.astralibs.AstraLibs
import ru.astrainteractive.astralibs.Logger
import ru.astrainteractive.astralibs.di.IDependency
import ru.astrainteractive.astralibs.di.getValue


class CraftingApi(
    empireItemsApi: IDependency<EmpireItemsAPI>,
) {
    private val empireItemsApi by empireItemsApi


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
            addAll(recipesMap[result] ?: listOf())
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