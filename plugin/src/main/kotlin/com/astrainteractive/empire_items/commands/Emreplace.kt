package com.astrainteractive.empire_items.commands

import com.astrainteractive.empire_items.modules.TranslationModule
import ru.astrainteractive.astralibs.Logger
import ru.astrainteractive.astralibs.commands.AstraDSLCommand
import com.astrainteractive.empire_itemss.api.EmpireItemsAPI.empireID
import com.astrainteractive.empire_itemss.api.EmpireItemsAPI.toAstraItem
import com.astrainteractive.empire_items.util.Translations
import org.bukkit.entity.Player
fun CommandManager.emReplace() = AstraDSLCommand.command("emreplace") {
    if (sender !is Player) {
        Logger.warn("Player only command", tag = CommandManager.TAG)
        return@command
    }

    val p = sender as Player
    var item = p.inventory.itemInMainHand
    val id = item.empireID ?: return@command
    val amount = item.amount
    val durability = item.durability
    item = id.toAstraItem(amount) ?: return@command
    item.durability = durability
    (sender as Player).inventory.setItemInMainHand(item)
    sender.sendMessage(TranslationModule.value.itemReplaced)
}
