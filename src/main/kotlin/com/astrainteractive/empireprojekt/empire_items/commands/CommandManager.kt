package com.astrainteractive.empireprojekt.empire_items.commands

import com.astrainteractive.astralibs.AstraUtils
import com.astrainteractive.astralibs.HEX
import com.astrainteractive.astralibs.runAsyncTask
import com.astrainteractive.empireprojekt.EmpirePlugin
import com.astrainteractive.empireprojekt.essentials.sit.SitEvent
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType
import com.astrainteractive.empireprojekt.empire_items.util.EmpirePermissions
import com.astrainteractive.empireprojekt.empire_items.util.EmpireUtils
import java.io.File

class CommandManager() : CommandExecutor {

    private var plugin: EmpirePlugin = EmpirePlugin.instance
    private var tabCompletion: EmpireTabCompleter = EmpireTabCompleter()

    init {
        plugin.getCommand("emnpc")!!.tabCompleter = tabCompletion
        plugin.getCommand("emspawn")!!.tabCompleter = tabCompletion
        plugin.getCommand("emspawn")!!.setExecutor(this)
        plugin.getCommand("emoji")!!.tabCompleter = tabCompletion
        plugin.getCommand("emoji")!!.setExecutor(this)
        plugin.getCommand("ezip")!!.setExecutor(this)
        plugin.getCommand("emgui")!!.setExecutor(this)
        plugin.getCommand("emsounds")!!.setExecutor(this)
        plugin.getCommand("emreplace")!!.setExecutor(this)
        plugin.getCommand("emojis")!!.setExecutor(this)
        plugin.getCommand("emp")!!.setExecutor(this)
        plugin.getCommand("empireitems")!!.setExecutor(this)
        plugin.getCommand("ezip")!!.setExecutor(this)
        plugin.getCommand("empack")!!.setExecutor(this)
        plugin.getCommand("sit")!!.setExecutor(this)
        AstraGuiCommand()
        AstraItemCommand()
        EmojiBook()
        Emreplace()
        Ezip()
        Reload()
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (label.equals("emoji", ignoreCase = true))
            sendChatEmoji(sender, args)


        if (label.equals("sit", ignoreCase = true))
            if (sender is Player)
                SitEvent.instance.sitPlayer(sender)



        if (label.equals("empack", ignoreCase = true))
            if (sender is Player)
                sender.setResourcePack(EmpirePlugin.empireConfig.resourcePackRef ?: return true)

        return false
    }


    /**
     * Преобразует сообщение с эмодзи в чат
     */
    fun sendChatEmoji(sender: CommandSender, args: Array<out String>) {
        if (sender !is Player)
            return
        val chat = args.joinToString(" ").HEX()
        sender.chat(chat)
        return
    }

}