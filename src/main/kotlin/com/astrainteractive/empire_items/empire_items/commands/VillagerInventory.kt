package com.astrainteractive.empire_items.empire_items.commands

import com.astrainteractive.astralibs.async.AsyncHelper
import com.astrainteractive.astralibs.commands.AstraDSLCommand
import com.astrainteractive.astralibs.events.DSLEvent
import com.astrainteractive.astralibs.menu.AstraMenuSize
import com.astrainteractive.astralibs.utils.HEX
import com.astrainteractive.empire_items.api.EmpireItemsAPI
import com.astrainteractive.empire_items.empire_items.util.emoji
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.MerchantInventory
import org.bukkit.inventory.MerchantRecipe

fun CommandManager.villagerInventoryAutoComplete() = AstraDSLCommand.onTabComplete("villager_inventory") {
    return@onTabComplete EmpireItemsAPI.itemYamlFiles
        .mapNotNull { it.merchant_recipes }
        .flatMap { it.values.map { it.id } }
        .toSet()
        .toList()
}

fun CommandManager.villagerInventory() = AstraDSLCommand.command("villager_inventory") {
    val trades = EmpireItemsAPI.itemYamlFiles.mapNotNull { it.merchant_recipes }
    val id = this.getArgumentOrNull(0) ?: kotlin.run {
        sender.sendMessage("Wrong arguments. Avaliable trades: ${trades.flatMap { it.keys }.toSet()}")
        return@command
    }

    val trade = trades.firstOrNull { it.firstNotNullOf { it.value.id == id } } ?: run {
        sender.sendMessage("Wrong arguments. Avaliable trades: ${trades.flatMap { it.keys }.toSet()}")
        return@command
    }

    val merchantRecipes = trade.values.flatMap { it.recipes.values }.mapNotNull { it.toMerchantRecipe() }
    (this.sender as Player).openMerchant(Bukkit.createMerchant(trade.values.firstOrNull()?.title?.HEX()?.emoji()?:"").apply {
        this.recipes = merchantRecipes
    }, true)
}


