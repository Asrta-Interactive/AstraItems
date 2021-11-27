package com.astrainteractive.empireprojekt.empire_items.api.crafting

import com.astrainteractive.empireprojekt.empire_items.api.items.data.ItemManager
import com.astrainteractive.empireprojekt.empire_items.api.items.data.ItemManager.getAstraID
import com.astrainteractive.empireprojekt.empire_items.api.items.data.ItemManager.toAstraItemOrItem
import com.astrainteractive.empireprojekt.empire_items.api.utils.BukkitConstants
import com.astrainteractive.astralibs.AstraLibs
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.inventory.RecipeChoice
import org.bukkit.inventory.ShapelessRecipe
import java.lang.IllegalStateException

data class AstraShapelessRecipe(
    val id: String,
    val input: String,
    val result: String,
    val amount: Int
) {


    fun createRecipe() {
        val namespacedKey = NamespacedKey(AstraLibs.instance, BukkitConstants.ASTRA_CRAFTING+id)
        val resultItem = result.toAstraItemOrItem(amount)?:return
        val inputItem = input.toAstraItemOrItem()?:return
        val recipeChoise =
            if (inputItem.getAstraID() != null) RecipeChoice.ExactChoice(inputItem) else RecipeChoice.MaterialChoice(
                inputItem.type
            )


        val shapelessRecipe = ShapelessRecipe(namespacedKey, resultItem)
        shapelessRecipe.addIngredient(recipeChoise)

        try {
            Bukkit.addRecipe(shapelessRecipe)
            ItemManager.addRecipe(result,shapelessRecipe)
        } catch (e: IllegalStateException) {
            println("Не удалось добавить крафт ${id} ${e.message}")
        }

    }

    companion object {
        fun getAllRecipes(s: ConfigurationSection?) =
            s?.getKeys(false)?.mapNotNull {
                getRecipe(s.getConfigurationSection(it))
            }

        fun getRecipe(s: ConfigurationSection?): AstraShapelessRecipe? {
            val id = "s_" + (s?.name ?: return null)
            val input = s.getString("input") ?: return null
            val result = s.getString("result") ?: return null
            val amount = s.getInt("amount", 1)
            return AstraShapelessRecipe(id = id, input = input, result = result, amount = amount)
        }
    }
}