package com.makeevrserg.empireprojekt.empire_items.commands

import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.essentials.sit.SitEvent
import com.makeevrserg.empireprojekt.empirelibs.menu.PlayerMenuUtility
import com.makeevrserg.empireprojekt.empire_items.emgui.EmpireCategoriesMenu
import com.makeevrserg.empireprojekt.empire_items.emgui.EmpireSoundsMenu
import com.makeevrserg.empireprojekt.empire_items.util.BetterConstants
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType
import com.makeevrserg.empireprojekt.empire_items.util.EmpirePermissions
import com.makeevrserg.empireprojekt.empire_items.util.ResourcePackNew
import com.makeevrserg.empireprojekt.empirelibs.EmpireUtils
import com.makeevrserg.empireprojekt.empirelibs.HEX
import java.io.File

class CommandManager() : CommandExecutor {

    private var plugin: EmpirePlugin = EmpirePlugin.instance
    private var tabCompletion: EmpireTabCompleter = EmpireTabCompleter()

    init {
        plugin.getCommand("emp")!!.tabCompleter = tabCompletion
        plugin.getCommand("emnpc")!!.tabCompleter = tabCompletion
        plugin.getCommand("emspawn")!!.tabCompleter = tabCompletion
        plugin.getCommand("emspawn")!!.setExecutor(this)
        plugin.getCommand("erandomitem")!!.setExecutor(RandomItem())
        plugin.getCommand("erandomitem")!!.tabCompleter = tabCompletion
        plugin.getCommand("emoji")!!.tabCompleter = tabCompletion
        plugin.getCommand("emoji")!!.setExecutor(this)
        plugin.getCommand("ereload")!!.setExecutor(this)
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
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (sender.hasPermission(EmpirePermissions.EMPGIVE) && (label.equals("emp", true)))
            if (args.size >= 3 && args[0].equals("give", ignoreCase = true))
                giveItem(sender, args[1], args[2], args.getOrNull(3)?.toIntOrNull() ?: 1)

        if (label.equals("emoji", ignoreCase = true))
            sendChatEmoji(sender, args)


        if (label.equals("sit", ignoreCase = true))
            if (sender is Player)
                SitEvent.instance.sitPlayer(sender)

        if (label.equals("emgui", ignoreCase = true))
            EmpireCategoriesMenu(PlayerMenuUtility(sender as Player)).open()


        if (label.equals("emsounds", ignoreCase = true))
            EmpireSoundsMenu(PlayerMenuUtility(sender as Player)).open()


        if (label.equals("empack", ignoreCase = true))
            if (sender is Player)
                sender.setResourcePack(EmpirePlugin.empireConfig.resourcePackRef ?: return true)

        if (label.equals("emreplace", ignoreCase = true))
            if (sender is Player)
                if (emreplace(sender))
                    sender.sendMessage(EmpirePlugin.translations.ITEM_REPLACED)
                else
                    sender.sendMessage(EmpirePlugin.translations.ITEM_REPLACE_WRONG)

        if (label.equals("emojis", ignoreCase = true))
            if (sender is Player)
                getEmojiBook(sender)

        if (label.equals("ezip", ignoreCase = true) && sender.hasPermission(EmpirePermissions.EZIP))
            eZip(sender)

        if (label.equals("ereload", ignoreCase = true) && sender.hasPermission(EmpirePermissions.RELOAD))
            eReload(sender)

        return false
    }


    /**
     * Выдает игроку предмет
     */
    private fun giveItem(sender: CommandSender, playerName: String, item: String, count: Int) {
        if (EmpirePlugin.empireItems.empireItems.containsKey(item)) {
            val player: Player = Bukkit.getPlayer(playerName) ?: return
            player.sendMessage(EmpirePlugin.translations.ITEM_GAINED + "$count $item")
            sender.sendMessage(EmpirePlugin.translations.ITEM_GIVE + " $item:$count -> $playerName")
            val itemStack = EmpirePlugin.empireItems.empireItems[item]?.clone() ?: return
            itemStack.amount = count
            player.inventory.addItem(itemStack)
        } else
            sender.sendMessage(EmpirePlugin.translations.ITEM_NOT_FOUND + item)

    }

    /**
     * Заменяет предмет в главной руке
     */
    fun emreplace(sender: Player): Boolean {
        var item = sender.inventory.itemInMainHand
        val meta = item.itemMeta ?: return false
        val id = meta.persistentDataContainer.get(BetterConstants.EMPIRE_ID.value, PersistentDataType.STRING)
            ?: return false

        val amount = item.amount

        val durability = item.durability
        item = EmpirePlugin.empireItems.empireItems[id]?.clone() ?: return true
        item.amount = amount
        item.durability = durability
        sender.inventory.setItemInMainHand(item)
        return true
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


    /**
     * Перезагрузка плагина
     */
    private fun eReload(sender: CommandSender) {
        sender.sendMessage(EmpirePlugin.translations.RELOAD)
        plugin.disablePlugin()
        plugin.initPlugin()
        sender.sendMessage(EmpirePlugin.translations.RELOAD_COMPLETE)
    }

    /**
     * Создание архива
     */
    private fun eZip(sender: CommandSender) {
        sender.sendMessage(EmpirePlugin.translations.ZIP_START)
        ResourcePackNew()
        if (ResourcePackNew.zipAll(
                plugin.dataFolder.toString() + File.separator + "pack",
                plugin.dataFolder.toString() + File.separator + "pack" + File.separator + "pack.zip"
            )
        ) {


            sender.sendMessage(EmpirePlugin.translations.ZIP_SUCCESS)
        } else
            sender.sendMessage(EmpirePlugin.translations.ZIP_ERROR)
    }

    /**
     * Создает и показывает игроку книгу с эмодзи
     */
    private fun getEmojiBook(sender: Player) {
        var list = ""
        for (emoji in EmpirePlugin.empireFonts._fontInfoValueById.keys) {
            if (EmpirePlugin.empireFonts._fontInfoValueById[emoji]?.sendBlocked ?: continue)
                continue
            list += EmpireUtils.HEXPattern("&r${emoji}\n&r&f${EmpirePlugin.empireFonts._fontInfoValueById[emoji]!!.chars}&r\n")
        }

        val book =
            EmpireUtils.getBook("RomaRoman", EmpireUtils.HEXPattern("&fЭмодзи"), mutableListOf(list), false)
        sender.openBook(book)
    }
}