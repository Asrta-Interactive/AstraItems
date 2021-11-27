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
import java.lang.IllegalStateException

data class AstraFurnaceRecipe(
    val id: String,
    val result: String,
    val input: String,
    val cookTime: Int,
    val exp: Int,
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
        val furnaceRecipe = org.bukkit.inventory.FurnaceRecipe(
            namespacedKey,
            resultItem,
            recipeChoise,
            exp.toFloat(),
            cookTime
        )
        try {
            Bukkit.addRecipe(furnaceRecipe)
            ItemManager.addRecipe(result,furnaceRecipe)
        } catch (e: IllegalStateException) {
            println("Не удалось добавить крафт ${id} ${e.message}")
        }

    }
    companion object {
        fun getAllRecipes(s: ConfigurationSection?) =
            s?.getKeys(false)?.mapNotNull {
                getRecipe(s.getConfigurationSection(it))
            }

        fun getRecipe(s: ConfigurationSection?): AstraFurnaceRecipe? {
            val id = "f_" + (s?.name ?: return null)
            val input = s.getString("input") ?: return null
            val result = s.getString("result") ?: return null
            val cookTime = s.getInt("cookTime", 20)
            val exp = s.getInt("exp", 0)
            val amount = s.getInt("amount", 1)
            return AstraFurnaceRecipe(
                id = id,
                input = input,
                result = result,
                cookTime = cookTime,
                exp = exp,
                amount = amount
            )
        }
    }
}