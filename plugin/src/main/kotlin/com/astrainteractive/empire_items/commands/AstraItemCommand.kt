package com.astrainteractive.empire_items.commands

import com.astrainteractive.empire_items.modules.TranslationModule
import ru.astrainteractive.astralibs.AstraLibs
import ru.astrainteractive.astralibs.utils.registerCommand
import ru.astrainteractive.astralibs.utils.registerTabCompleter
import ru.astrainteractive.astralibs.utils.withEntry
import com.astrainteractive.empire_itemss.api.EmpireItemsAPI
import com.astrainteractive.empire_itemss.api.EmpireItemsAPI.toAstraItemOrItem
import com.astrainteractive.empire_items.util.EmpirePermissions
import com.astrainteractive.empire_items.util.Translations
import com.astrainteractive.empire_items.util.Translations.Companion.argumentMessage
import com.astrainteractive.empire_items.util.Translations.Companion.sendTo
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class AstraItemCommand {

    val translations: Translations
        get() = TranslationModule.value

    private val commandExecutor = AstraLibs.registerCommand("emp") { sender, args ->

        if (!sender.hasPermission(EmpirePermissions.EMPGIVE)) {
            sender.sendMessage(translations.noPerms)
            return@registerCommand
        }
        if (sender !is Player) {
            sender.sendMessage(translations.notPlayer)
            return@registerCommand
        }
        if (args.getOrNull(0).equals("give", ignoreCase = true)) {
            val playerName = args.getOrNull(1)
            val id = args.getOrNull(2)
            val amount = args.getOrNull(3)?.toIntOrNull() ?: 1
            val itemStack = id.toAstraItemOrItem(amount) ?: run {
                sender.sendMessage(translations.itemNotExist)
                return@registerCommand
            }
            val player = playerName?.let(Bukkit::getPlayer) ?: run {
                sender.sendMessage(translations.playerNotFound)
                return@registerCommand
            }
            translations.itemGave
                .argumentMessage("%player%" to player.name)
                .argumentMessage("%item%" to itemStack.itemMeta.displayName)
                .sendTo(sender)

            translations.itemGained
                .argumentMessage("%player%" to player.name)
                .argumentMessage("%item%" to itemStack.itemMeta.displayName)
                .sendTo(sender)

            player.inventory.addItem(itemStack)
        }
    }
    private val tabCompleter = AstraLibs.registerTabCompleter("emp") { _, args ->
        when (args.size) {
            1 -> return@registerTabCompleter listOf("give").withEntry(args[0])
            2 -> return@registerTabCompleter Bukkit.getOnlinePlayers().map { it.name }.withEntry(args[1])
            3 -> return@registerTabCompleter EmpireItemsAPI.itemYamlFilesByID.keys.toList().withEntry(args[2])
        }
        return@registerTabCompleter listOf()

    }
}