package com.astrainteractive.empireprojekt.empire_items.commands

import com.astrainteractive.astralibs.*
import com.astrainteractive.empireprojekt.EmpirePlugin
import com.astrainteractive.empireprojekt.empire_items.util.Config
import com.astrainteractive.empireprojekt.essentials.sit.SitEvent
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import kotlin.random.Random

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
        AstraLibs.registerCommand("edice"){ sender, args->
            if (sender !is Player)
                return@registerCommand
            val p = sender as Player
            val nearestP = Bukkit.getOnlinePlayers().filter { p.location.distance(it.location)<30 }
            nearestP.forEach { it.sendMessage("#03b6fcИгрок #fc8803${p.name} #03b6fcбросил кубик. Значение: #fc8803${Random(System.currentTimeMillis()).nextInt(1,6+1)}".HEX()) }

        }
        AstraGuiCommand()
        AstraItemCommand()
        EmojiBook()
        Emreplace()
        Ezip()
        Reload()
        AstraLibs.registerCommand("egeneration") { sender, args ->
//            sender.sendMessage("#dbac0fТекущая очередь генерирующихся блоков: #0f8ddb${BlockGenerationEvent.que}".HEX())
        }
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (label.equals("emoji", ignoreCase = true))
            sendChatEmoji(sender, args)


        if (label.equals("sit", ignoreCase = true))
            if (sender is Player)
                SitEvent.instance.sitPlayer(sender)



        if (label.equals("empack", ignoreCase = true))
            if (sender is Player)
                sender.setResourcePack(Config.resourcePackLink ?: return true)

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