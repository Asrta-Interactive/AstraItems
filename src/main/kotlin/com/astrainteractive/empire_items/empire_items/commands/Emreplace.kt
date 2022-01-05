package com.astrainteractive.empire_items.empire_items.commands

import com.astrainteractive.astralibs.AstraLibs
import com.astrainteractive.astralibs.registerCommand
import com.astrainteractive.empire_items.EmpirePlugin
import com.astrainteractive.empire_items.empire_items.api.items.data.ItemManager.getAstraID
import com.astrainteractive.empire_items.empire_items.api.items.data.ItemManager.getItemStack
import org.bukkit.entity.Player

class Emreplace {
    init {
        AstraLibs.registerCommand("emreplace") { sender, args ->
            if (sender !is Player)
                return@registerCommand
            val p = sender as Player
            var item = p.inventory.itemInMainHand
            val id = item.getAstraID()?:return@registerCommand
            val amount = item.amount
            val durability = item.durability
            item = id.getItemStack(amount)?:return@registerCommand
            item.durability = durability
            sender.inventory.setItemInMainHand(item)
            sender.sendMessage(EmpirePlugin.translations.itemReplaced)
        }
    }
}