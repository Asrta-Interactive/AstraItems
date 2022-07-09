package com.astrainteractive.empire_items.empire_items.commands;

import com.astrainteractive.astralibs.AstraLibs
import com.astrainteractive.astralibs.HEX
import com.astrainteractive.astralibs.async.AsyncHelper
import com.astrainteractive.astralibs.menu.AstraPlayerMenuUtility
import com.astrainteractive.astralibs.registerCommand
import com.astrainteractive.empire_items.modules.boss_fight.PlayersInviteMenu
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bukkit.entity.Player


fun CommandManager.playerInvite() = AstraLibs.registerCommand("player_invite") { sender, args ->
    if (sender !is Player) return@registerCommand
    if (args.getOrNull(0)!="asd@uasdfq2AKSDk"){
        sender.sendMessage("Вы не ввели секретный код")
        return@registerCommand
    }
    sender.sendMessage("&7Инвентарь открывается... Подождите...".HEX())
    AsyncHelper.launch(Dispatchers.IO) {
        PlayersInviteMenu(AstraPlayerMenuUtility(sender)).open()
    }
}