package com.astrainteractive.empire_items.empire_items.api.crafting

import com.astrainteractive.astralibs.AstraLibs
import com.astrainteractive.astralibs.Logger
import com.astrainteractive.empire_items.empire_items.api.items.data.ItemManager
import com.astrainteractive.empire_items.empire_items.api.items.data.ItemManager.getAstraID
import com.astrainteractive.empire_items.empire_items.api.utils.BukkitConstants
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe
import org.bukkit.inventory.RecipeChoice

object CraftingManager {
    private var crafting: Crafting = Crafting()
    private val keyMap = mutableMapOf<String, MutableList<NamespacedKey>>()
    fun addToMap(id: String, key: NamespacedKey) {
        if (!keyMap.containsKey(id))
            keyMap[id] = mutableListOf()
        val list = keyMap[id]!!
        list.add(key)
        keyMap[id] = list
    }
    fun getFurnaceByInputId(id: String) = crafting.furnace.filter { it.input==id }
    fun getKeysById(id: String) = keyMap[id]

    fun load() {
        clear()
        crafting = Crafting.getCrafting().apply { createRecipes() }
    }

    fun clear() {
        crafting.clear()
    }

    fun createKey(id:String): NamespacedKey {
        val key = NamespacedKey(AstraLibs.instance, BukkitConstants.ASTRA_CRAFTING + id)
        addToMap(id,key)
        return key
    }
    fun getRecipeChoice(inputItem: ItemStack) = if (inputItem.getAstraID() != null)
        RecipeChoice.ExactChoice(inputItem)
    else RecipeChoice.MaterialChoice(inputItem.type)
    fun addRecipe(id: String, result: String, recipe: Recipe) = try {
        Bukkit.addRecipe(recipe)
        ItemManager.addRecipe(result, recipe)
    } catch (e: IllegalStateException) {
        Logger.warn("Не удалось добавить крафт id:${id} result:${result}. ID не должны повтаряться! ${e.message}", "Crafting")
    }

    fun usedInCraft(id: String): MutableSet<String> {
        val craftingTable =
            crafting.craftingTable.filter { it.ingredients?.values?.contains(id) == true }.map { it.result }
        val shapeless = crafting.shapeless.filter { it.input == id }.map { it.result }
        val furnace = crafting.furnace.filter { it.input == id }.map { it.result }
        val player = crafting.player.filter { it.ingredients?.values?.contains(id) == true }.map { it.result }
        val set = mutableSetOf<String>()
        set.addAll(craftingTable)
        set.addAll(shapeless)
        set.addAll(furnace)
        set.addAll(player)
        return set
    }
}