package com.astrainteractive.empire_items.commands

import com.astrainteractive.empire_items.modules.TranslationModule
import ru.astrainteractive.astralibs.Logger
import com.astrainteractive.empire_itemss.api.EmpireItemsAPI.empireID
import com.astrainteractive.empire_itemss.api.EmpireItemsAPI.toAstraItem
import com.astrainteractive.empire_items.util.Translations
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.AstraLibs
import ru.astrainteractive.astralibs.utils.registerCommand

fun CommandManager.emReplace() = AstraLibs.registerCommand("emreplace") {sender,args->
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
    sender.sendMessage(TranslationModule.value.itemReplaced)
}
