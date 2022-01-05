package com.astrainteractive.empireprojekt.empire_items.commands

import com.astrainteractive.astralibs.AstraLibs
import com.astrainteractive.astralibs.registerCommand
import com.astrainteractive.astralibs.registerTabCompleter
import com.astrainteractive.empireprojekt.EmpirePlugin
import com.astrainteractive.empireprojekt.empire_items.api.items.data.ItemManager
import com.astrainteractive.empireprojekt.empire_items.api.items.data.ItemManager.toAstraItemOrItem
import com.astrainteractive.empireprojekt.empire_items.util.EmpirePermissions
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class AstraItemCommand {

    fun Array<out Any>.equals(e: Any, position: Int) = this.getOrNull(position)?.equals(e) ?: false

    private fun commandExecutor() = AstraLibs.registerCommand("emp") { sender, args ->
        if (!sender.hasPermission(EmpirePermissions.EMPGIVE))
            return@registerCommand
        if (sender !is Player)
            return@registerCommand
        // /emp give RomaRoman item 1
        if (args.equals("give", 0)) {
            val playerName = args.getOrNull(1)?:return@registerCommand
            val id = args.getOrNull(2)
            val amount = args.getOrNull(3)?.toIntOrNull() ?: 1
            val itemStack = id.toAstraItemOrItem(amount)
            if (itemStack == null) {
                sender.sendMessage("Такого предмета нет")
                return@registerCommand
            }
            val player = Bukkit.getPlayer(playerName)?:return@registerCommand
            sender.sendMessage(EmpirePlugin.translations.itemGave+" ${player.name}"+" ${itemStack.itemMeta?.displayName}")
            player.inventory?.addItem(itemStack)
            player.sendMessage(EmpirePlugin.translations.itemGained+" ${itemStack.itemMeta?.displayName}")
        }
    }
    private fun tabCompleter() = AstraLibs.registerTabCompleter("emp"){sender,args->
        when(args.size) {
            1->return@registerTabCompleter listOf("give")
            2->return@registerTabCompleter Bukkit.getOnlinePlayers().map { it.name }
            3->return@registerTabCompleter ItemManager.getItemsIDS()
        }
        return@registerTabCompleter listOf()

    }

    init {
        commandExecutor()
        tabCompleter()
    }
}