package com.astrainteractive.empire_items.empire_items.commands

import com.astrainteractive.astralibs.AstraLibs
import com.astrainteractive.astralibs.registerCommand
import com.astrainteractive.empire_items.EmpirePlugin
import com.astrainteractive.empire_items.api.EmpireItemsAPI.empireID
import com.astrainteractive.empire_items.api.EmpireItemsAPI.toAstraItem
import com.astrainteractive.empire_items.api.EmpireItemsAPI.toAstraItemOrItem
import org.bukkit.entity.Player

class Emreplace {
    init {
        AstraLibs.registerCommand("emreplace") { sender, args ->
            if (sender !is Player)
                return@registerCommand
            val p = sender as Player
            var item = p.inventory.itemInMainHand
            val id = item.empireID?:return@registerCommand
            val amount = item.amount
            val durability = item.durability
            item = id.toAstraItem(amount)?:return@registerCommand
            item.durability = durability
            sender.inventory.setItemInMainHand(item)
            sender.sendMessage(EmpirePlugin.translations.itemReplaced)
        }
    }
}