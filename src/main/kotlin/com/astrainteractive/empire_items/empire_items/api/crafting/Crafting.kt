package com.astrainteractive.empire_items.empire_items.api.crafting

import com.astrainteractive.empire_items.empire_items.api.utils.BukkitConstants
import com.astrainteractive.empire_items.empire_items.api.utils.getCustomItemsFiles
import com.astrainteractive.astralibs.AstraLibs
import org.bukkit.NamespacedKey
import org.bukkit.inventory.*

data class Crafting(
    val craftingTable: MutableList<AstraCraftingTableRecipe> = mutableListOf(),
    val shapeless: MutableList<AstraShapelessRecipe> = mutableListOf(),
    val furnace: MutableList<AstraFurnaceRecipe> = mutableListOf(),
    val player: MutableList<AstraCraftingTableRecipe> = mutableListOf()
) {
    fun clear(){

        fun isCustomRecipe(key: NamespacedKey): Boolean {
            val k = key.key.contains(BukkitConstants.ASTRA_CRAFTING)
            return k
        }
        fun isCustomRecipe(recipe: FurnaceRecipe) = isCustomRecipe(recipe.key)
        fun isCustomRecipe(recipe: ShapedRecipe) = isCustomRecipe(recipe.key)
        fun isCustomRecipe(recipe: ShapelessRecipe) = isCustomRecipe(recipe.key)

        fun isCustomRecipe(recipe: Recipe): Boolean {
            return when (recipe) {
                is FurnaceRecipe -> isCustomRecipe(recipe)
                is ShapedRecipe -> isCustomRecipe(recipe)
                is ShapelessRecipe -> isCustomRecipe(recipe)
                else -> false
            }
        }

        val ite = AstraLibs.instance.server.recipeIterator()
        var recipe: Recipe?
        while (ite.hasNext()) {
            recipe = ite.next()
            if (isCustomRecipe(recipe)) {
                ite.remove()
                continue
            }
        }



        craftingTable.clear()
        shapeless.clear()
        furnace.clear()
        player.clear()
    }
    fun createRecipes() {
        craftingTable.forEach {
            it.createRecipe()
        }
        shapeless.forEach {
            it.createRecipe()
        }
        furnace.forEach {
            it.createRecipe()
        }
        player.forEach {
            it.createRecipe()
        }
    }


    companion object {
        fun getCrafting(): Crafting {
            val craftingTable = mutableListOf<AstraCraftingTableRecipe>()
            val shapeless = mutableListOf<AstraShapelessRecipe>()
            val furnace = mutableListOf<AstraFurnaceRecipe>()
            val player = mutableListOf<AstraCraftingTableRecipe>()
            getCustomItemsFiles()?.forEach {
                val fileConfig = it.getConfig()
                val _craftingTable =
                    AstraCraftingTableRecipe.getAllRecipes(fileConfig.getConfigurationSection("crafting_table"))
                        ?: listOf()
                val _shapeless =
                    AstraShapelessRecipe.getAllRecipes(fileConfig.getConfigurationSection("shapeless")) ?: listOf()
                val _furnace =
                    AstraFurnaceRecipe.getAllRecipes(fileConfig.getConfigurationSection("furnace")) ?: listOf()
                val _player =
                    AstraCraftingTableRecipe.getAllRecipes(fileConfig.getConfigurationSection("player")) ?: listOf()
                craftingTable.addAll(_craftingTable)
                shapeless.addAll(_shapeless)
                furnace.addAll(_furnace)
                player.addAll(_player)
            }
            return Crafting(craftingTable = craftingTable,shapeless = shapeless,furnace = furnace,player = player)
        }
    }


}