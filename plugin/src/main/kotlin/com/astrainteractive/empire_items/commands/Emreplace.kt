package com.astrainteractive.empire_items.commands

import com.astrainteractive.empire_items.di.TranslationModule
import com.astrainteractive.empire_items.util.ext_api.toAstraItem
import com.astrainteractive.empire_items.api.utils.empireID
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.AstraLibs
import ru.astrainteractive.astralibs.Logger
import ru.astrainteractive.astralibs.commands.registerCommand
import ru.astrainteractive.astralibs.di.getValue

fun CommandManager.emReplace() = AstraLibs.instance.registerCommand("emreplace") {
    val translation by TranslationModule
    if (sender !is Player) {
        Logger.warn(message="Player only command", tag = CommandManager.TAG)
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
