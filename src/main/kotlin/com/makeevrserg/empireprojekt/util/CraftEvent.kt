package com.makeevrserg.empireprojekt.util

import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.EmpirePlugin.Companion.instance
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.inventory.*
import java.lang.IllegalStateException

class CraftEvent {


    private val fileConfig = EmpirePlugin.empireFiles.craftingFile.getConfig()

    private var _empireRecipies: MutableMap<String, EmpireRecipe> = mutableMapOf()

    class EmpireRecipe {
        val craftingTable: MutableList<ShapedRecipe> = mutableListOf()
        val furnace: MutableList<FurnaceRecipe> = mutableListOf()
    }


    val empireRecipies: MutableMap<String, EmpireRecipe>
        get() = _empireRecipies

    private fun generateCraftingTable() {
        fileConfig ?: return
        val craftingSection = fileConfig.getConfigurationSection("crafting_table") ?: return
        for (key in craftingSection.getKeys(false)) {
            val itemSection = craftingSection.getConfigurationSection(key)!!
            val itemId = itemSection.getString("result") ?: key
            val resultItem = EmpirePlugin.empireItems.empireItems[itemId] ?: ItemStack(Material.getMaterial(itemId)?:continue)

            addRecipe(itemId, createRecipies(resultItem, key, itemSection)?:continue)
        }

    }

    private fun generateFurnaceRecipe() {
        fileConfig ?: return
        val craftingSection = fileConfig.getConfigurationSection("furnace") ?: return
        for (key in craftingSection.getKeys(false)) {
            val itemSection = craftingSection.getConfigurationSection(key)!!
            val itemId = itemSection.getString("result") ?: key
            val resultItem = EmpirePlugin.empireItems.empireItems[key] ?: continue
            val cookTime = itemSection.getInt("cook_time", 200)
            val exp = itemSection.getInt("exp")
            resultItem.amount = itemSection.getInt("amount", 1)
            val inputID = itemSection.getString("input") ?: continue
            val inputItemStack: ItemStack =
                EmpirePlugin.empireItems.empireItems[inputID] ?: ItemStack(Material.getMaterial(inputID) ?: continue)
            val recipe = FurnaceRecipe(
                NamespacedKey(instance, EmpirePlugin.empireConstants.CUSTOM_RECIPE_KEY+key),
                resultItem,
                RecipeChoice.ExactChoice(inputItemStack),
                exp.toFloat(),
                cookTime
            )

            Bukkit.addRecipe(recipe)
            addRecipe(itemId, recipe)
        }

    }

    private fun createRecipies(itemStack: ItemStack, itemID: String, itemConfig: ConfigurationSection): ShapedRecipe? {
        val resultItemStack = itemStack.clone()
        resultItemStack.amount = itemConfig.getInt("amount", 1)
        val ingrMap = mutableMapOf<Char, ItemStack>()
        for (key in itemConfig.getConfigurationSection("ingredients")!!.getKeys(false)) {
            val ingredient = itemConfig.getConfigurationSection("ingredients")!!.getString(key)!!
            ingrMap[key[0]] = EmpirePlugin.empireItems.empireItems[ingredient] ?: ItemStack(
                Material.getMaterial(ingredient) ?: return null
            )
        }

        val colList = mutableListOf<List<ItemStack>>()
        val pattern = itemConfig.getStringList("pattern")
        for (row in pattern) {
            val rowList = mutableListOf<ItemStack>()
            for (col in row) {
                if (col.toLowerCase() == 'x') {
                    rowList.add(ItemStack(Material.AIR))
                    continue
                }
                rowList.add(ingrMap[col] ?: return null)
            }
            colList.add(rowList)
        }

        val key = NamespacedKey(instance, EmpirePlugin.empireConstants.CUSTOM_RECIPE_KEY+itemID)
        val recipe = ShapedRecipe(key, resultItemStack)
        recipe.shape(pattern[0].toString(), pattern[1].toString(), pattern[2].toString())
        for (recChar in ingrMap.keys) {
            val item = ingrMap[recChar]?:continue
            val id = EmpireUtils.getEmpireID(item)
            var choice:RecipeChoice = RecipeChoice.MaterialChoice(item.type)
            if (id!=null)
                choice = RecipeChoice.ExactChoice(item)

            recipe.setIngredient(recChar, choice)
        }
        try {
            Bukkit.addRecipe(recipe)
        } catch (e:IllegalStateException){
            println(recipe.key)
        }

        return recipe

    }

    private fun addRecipe(id: String, recipe: Recipe) {

        if (_empireRecipies[id] == null)
            _empireRecipies[id] = EmpireRecipe()
        if (recipe is ShapedRecipe)
            _empireRecipies[id]!!.craftingTable.add(recipe)
        if (recipe is FurnaceRecipe)
            _empireRecipies[id]!!.furnace.add(recipe)

    }

    init {
        generateCraftingTable()
        generateFurnaceRecipe()
    }

    fun onDisable() {

    }
}