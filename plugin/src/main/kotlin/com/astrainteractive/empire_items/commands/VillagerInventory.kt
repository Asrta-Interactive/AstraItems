package com.astrainteractive.empire_items.commands

import ru.astrainteractive.astralibs.utils.HEX
import com.astrainteractive.empire_itemss.api.EmpireItemsAPI
import com.astrainteractive.empire_itemss.api.toMerchantRecipe
import com.astrainteractive.empire_itemss.api.utils.emoji
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.AstraLibs
import ru.astrainteractive.astralibs.utils.registerCommand
import ru.astrainteractive.astralibs.utils.registerTabCompleter

fun CommandManager.villagerInventoryAutoComplete() = AstraLibs.registerTabCompleter("villager_inventory") {sender,args->
    return@registerTabCompleter EmpireItemsAPI.itemYamlFiles
        .mapNotNull { it.merchant_recipes }
        .flatMap { it.values.map { it.id } }
        .toSet()
        .toList()
}

fun CommandManager.villagerInventory() = AstraLibs.registerCommand("villager_inventory") {sender,args->
    val trades = EmpireItemsAPI.itemYamlFiles.mapNotNull { it.merchant_recipes }
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


