package com.makeevrserg.empireprojekt.empire_items.util.crafting

import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.empire_items.util.BetterConstants
import com.makeevrserg.empireprojekt.empire_items.util.crafting.data.EmpireCraftingTableRecipe
import com.makeevrserg.empireprojekt.empire_items.util.crafting.data.EmpireFurnaceRecipe
import com.makeevrserg.empireprojekt.empire_items.util.crafting.data.EmpireShapelessRecipe
import com.makeevrserg.empireprojekt.empirelibs.EmpireYamlParser
import com.makeevrserg.empireprojekt.empirelibs.asEmpireItemOrItem
import com.makeevrserg.empireprojekt.empirelibs.getEmpireID
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.*
import java.lang.IllegalStateException

class CraftingManager {
    class EmpireRecipe {
        val craftingTable: MutableList<ShapedRecipe> = mutableListOf()
        val furnace: MutableList<org.bukkit.inventory.FurnaceRecipe> = mutableListOf()
        val shapeless: MutableList<ShapelessRecipe> = mutableListOf()
    }

    val empireRecipies: MutableMap<String, EmpireRecipe> = mutableMapOf()

    fun createCraftingTableRecipes() {
        val section =
            EmpirePlugin.empireFiles.craftingFile.getConfig().getConfigurationSection("crafting_table") ?: return
        for (id in section.getKeys(false)) {
            val obj = section.getConfigurationSection(id)
            val recipe =
                EmpireYamlParser.fromYAML<EmpireCraftingTableRecipe>(obj, EmpireCraftingTableRecipe::class.java)
                    ?: continue
            if (recipe.result == null)
                recipe.result = id

            val resultItem = recipe.result.asEmpireItemOrItem()?.clone() ?: continue

            resultItem.amount = recipe.amount ?: 1
            val key = NamespacedKey(EmpirePlugin.instance, BetterConstants.CUSTOM_RECIPE_KEY.name + id)
            val shapedRecipe = ShapedRecipe(key, resultItem)
            if (recipe.pattern.size == 3)
                shapedRecipe.shape(
                    recipe.pattern[0],
                    recipe.pattern[1],
                    recipe.pattern[2]
                )
            else
                shapedRecipe.shape(
                    recipe.pattern[0],
                    recipe.pattern[1]
                )

            for ((ch, item) in recipe.ingredients) {
                val itemStack = item.asEmpireItemOrItem()?.clone() ?: break
                val choice: RecipeChoice = if (itemStack.getEmpireID() != null)
                    RecipeChoice.ExactChoice(itemStack)
                else
                    RecipeChoice.MaterialChoice(itemStack.type)
                if (ch.equals('x', ignoreCase = true))
                    shapedRecipe.setIngredient(ch, RecipeChoice.MaterialChoice(Material.AIR))
                else
                    shapedRecipe.setIngredient(ch, choice)
            }
            try {
                Bukkit.addRecipe(shapedRecipe)
                if (empireRecipies[recipe.result] == null)
                    empireRecipies[recipe.result!!] = EmpireRecipe()
                empireRecipies[recipe.result]?.craftingTable?.add(shapedRecipe)


            } catch (e: IllegalStateException) {
                println("Не удалось добавить крафт ${id} ${e.message}")
            }
        }

    }

    fun createFurnaceRecipes() {

        val section =
            EmpirePlugin.empireFiles.craftingFile.getConfig().getConfigurationSection("furnace") ?: return
        for (id in section.getKeys(false)) {
            val obj = section.getConfigurationSection(id)
            val recipe =
                EmpireYamlParser.fromYAML<EmpireFurnaceRecipe>(obj, EmpireFurnaceRecipe::class.java) ?: continue
            if (recipe.result == null)
                recipe.result = id

            val resultItem = recipe.result.asEmpireItemOrItem()?.clone() ?: continue
            resultItem.amount = recipe.amount ?: 1
            val inputItem = recipe.input.asEmpireItemOrItem()?.clone() ?: continue

            val recipeChoise =
                if (inputItem.getEmpireID() != null) RecipeChoice.ExactChoice(inputItem) else RecipeChoice.MaterialChoice(
                    inputItem.type
                )


            val furnaceRecipe = org.bukkit.inventory.FurnaceRecipe(
                NamespacedKey(EmpirePlugin.instance, BetterConstants.CUSTOM_RECIPE_KEY.name + id),
                resultItem,
                recipeChoise,
                recipe.exp,
                recipe.cookTime
            )
            try {
                Bukkit.addRecipe(furnaceRecipe)
                if (empireRecipies[recipe.result] == null)
                    empireRecipies[recipe.result!!] = EmpireRecipe()
                empireRecipies[recipe.result]?.furnace?.add(furnaceRecipe)


            } catch (e: IllegalStateException) {
                println("Не удалось добавить крафт ${id} ${e.message}")
            }

        }
    }


    fun createShapelessRecipe() {
        val section =
            EmpirePlugin.empireFiles.craftingFile.getConfig().getConfigurationSection("shapeless") ?: return
        for (id in section.getKeys(false)) {
            val obj = section.getConfigurationSection(id)
            val recipe =
                EmpireYamlParser.fromYAML<EmpireShapelessRecipe>(obj, EmpireShapelessRecipe::class.java) ?: continue
            if (recipe.result == null)
                recipe.result = id

            val resultItem = recipe.result.asEmpireItemOrItem()?.clone() ?: continue
            resultItem.amount = recipe.amount ?: 1

            val inputItem = recipe.input.asEmpireItemOrItem()?.clone() ?: continue

            val key = NamespacedKey(EmpirePlugin.instance, BetterConstants.CUSTOM_RECIPE_KEY.name + id)
            val shapelessRecipe = ShapelessRecipe(key, resultItem)


            val choice: RecipeChoice = if (inputItem.getEmpireID() != null)
                RecipeChoice.ExactChoice(inputItem)
            else
                RecipeChoice.MaterialChoice(inputItem.type)

            shapelessRecipe.addIngredient(choice)
            try {
                Bukkit.addRecipe(shapelessRecipe)
                if (empireRecipies[recipe.result] == null)
                    empireRecipies[recipe.result!!] = EmpireRecipe()
                empireRecipies[recipe.result]?.shapeless?.add(shapelessRecipe)


            } catch (e: IllegalStateException) {
                println("Не удалось добавить крафт ${id} ${e.message}")
            }

        }
    }


    init {
        createCraftingTableRecipes()
        createFurnaceRecipes()
        createShapelessRecipe()
//        val furnaceRecipe = FurnaceRecipe.new()
    }
}