package com.astrainteractive.empire_items.commands

import com.astrainteractive.empire_items.di.empireItemsApiModule
import com.astrainteractive.empire_itemss.api.emoji
import com.astrainteractive.empire_itemss.api.models_ext.toMerchantRecipe
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.AstraLibs
import ru.astrainteractive.astralibs.commands.registerCommand
import ru.astrainteractive.astralibs.commands.registerTabCompleter
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astralibs.utils.HEX

private val empireItemsApi by empireItemsApiModule
fun CommandManager.villagerInventoryAutoComplete() = AstraLibs.instance.registerTabCompleter("villager_inventory") {
    return@registerTabCompleter empireItemsApi.itemYamlFiles
        .mapNotNull { it.merchant_recipes }
        .flatMap { it.values.map { it.id } }
        .toSet()
        .toList()
}

fun CommandManager.villagerInventory() = AstraLibs.instance.registerCommand("villager_inventory") {
    val trades = empireItemsApi.itemYamlFiles.mapNotNull { it.merchant_recipes }
    val id = args.getOrNull(0) ?: kotlin.run {
        sender.sendMessage("Wrong arguments. Avaliable trades: ${trades.flatMap { it.keys }.toSet()}")
        return@registerCommand
    }

    val trade = trades.firstOrNull { it.firstNotNullOf { it.value.id == id } } ?: run {
        sender.sendMessage("Wrong arguments. Avaliable trades: ${trades.flatMap { it.keys }.toSet()}")
        return@registerCommand
    }

    val merchantRecipes = trade.values.flatMap { it.recipes.values }.mapNotNull { it.toMerchantRecipe() }
    (sender as Player).openMerchant(Bukkit.createMerchant(trade.values.firstOrNull()?.title?.HEX()?.emoji()?:"").apply {
        this.recipes = merchantRecipes
    }, true)
}


