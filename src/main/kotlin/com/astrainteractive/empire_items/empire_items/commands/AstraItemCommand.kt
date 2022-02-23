package com.astrainteractive.empire_items.empire_items.commands

import com.astrainteractive.astralibs.AstraLibs
import com.astrainteractive.astralibs.registerCommand
import com.astrainteractive.astralibs.registerTabCompleter
import com.astrainteractive.astralibs.withEntry
import com.astrainteractive.empire_items.EmpirePlugin
import com.astrainteractive.empire_items.api.items.data.ItemApi
import com.astrainteractive.empire_items.api.items.data.ItemApi.toAstraItemOrItem
import com.astrainteractive.empire_items.empire_items.util.EmpirePermissions
import com.astrainteractive.empire_items.empire_items.util.Translations
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class AstraItemCommand {

    fun Array<out Any>.equals(e: Any, position: Int) = this.getOrNull(position)?.equals(e) ?: false

    private val commandExecutor = AstraLibs.registerCommand("emp") { sender, args ->
        if (!sender.hasPermission(EmpirePermissions.EMPGIVE)) {
            sender.sendMessage(Translations.instance.noPerms)
            return@registerCommand
        }
        if (sender !is Player) {
            sender.sendMessage(Translations.instance.notPlayer)
            return@registerCommand
        }
        if (args.equals("give", 0)) {
            val playerName = args.getOrNull(1) ?: return@registerCommand
            val id = args.getOrNull(2)
            val amount = args.getOrNull(3)?.toIntOrNull() ?: 1
            val itemStack = id.toAstraItemOrItem(amount)
            if (itemStack == null) {
                sender.sendMessage(Translations.instance.itemNotExist)
                return@registerCommand
            }
            val player = Bukkit.getPlayer(playerName)
            if (player==null){
                sender.sendMessage(Translations.instance.playerNotFound)
                return@registerCommand
            }
            sender.sendMessage(EmpirePlugin.translations.itemGave.replace("%player%",player.name).replace("%item%",itemStack.itemMeta.displayName))
            player.sendMessage(EmpirePlugin.translations.itemGained.replace("%player%",player.name).replace("%item%",itemStack.itemMeta.displayName))
            player.inventory.addItem(itemStack)
        }
    }
    private val tabCompleter = AstraLibs.registerTabCompleter("emp") { _, args ->
        when (args.size) {
            1 -> return@registerTabCompleter listOf("give").withEntry(args[0])
            2 -> return@registerTabCompleter Bukkit.getOnlinePlayers().map { it.name }.withEntry(args[1])
            3 -> return@registerTabCompleter ItemApi.getItemsIDS().withEntry(args[2])
        }
        return@registerTabCompleter listOf()

    }

}