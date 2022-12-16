package com.astrainteractive.empire_items.commands

import com.astrainteractive.empire_items.di.TranslationModule
import com.astrainteractive.empire_items.util.EmpireItemsAPIExt.toAstraItem
import com.astrainteractive.empire_itemss.api.empireID
import ru.astrainteractive.astralibs.Logger
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.AstraLibs
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astralibs.utils.registerCommand

fun CommandManager.emReplace() = AstraLibs.registerCommand("emreplace") {sender,args->
    val translation by TranslationModule
    if (sender !is Player) {
        Logger.warn("Player only command", tag = CommandManager.TAG)
        return@registerCommand
    }

    val p = sender as Player
    var item = p.inventory.itemInMainHand
    val id = item.empireID ?: return@registerCommand
    val amount = item.amount
    val durability = item.durability
    item = id.toAstraItem(amount) ?: return@registerCommand
    item.durability = durability
    (sender as Player).inventory.setItemInMainHand(item)
    sender.sendMessage(translation.itemReplaced)
}
