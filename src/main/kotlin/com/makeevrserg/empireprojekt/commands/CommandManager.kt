package com.makeevrserg.empireprojekt.commands

import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.essentials.sit.SitEvent
import com.makeevrserg.empireprojekt.menumanager.PlayerMenuUtility
import com.makeevrserg.empireprojekt.menumanager.menu.EmpireCategoriesMenu
import com.makeevrserg.empireprojekt.menumanager.menu.EmpireSoundsMenu
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BookMeta
import org.bukkit.persistence.PersistentDataType
import com.makeevrserg.empireprojekt.util.EmpirePermissions
import com.makeevrserg.empireprojekt.util.EmpireUtils
import com.makeevrserg.empireprojekt.util.ResourcePack
import java.io.File

class CommandManager() : CommandExecutor {

    private var plugin: EmpirePlugin = EmpirePlugin.plugin
    private var tabCompletion: EmpireTabCompleter = EmpireTabCompleter(plugin.empireItems.empireItems.keys.toList())

    init {
        plugin.getCommand("emp")!!.tabCompleter = tabCompletion
        plugin.getCommand("ereload")!!.setExecutor(this)
        plugin.getCommand("ezip")!!.setExecutor(this)
        plugin.getCommand("emgui")!!.setExecutor(this)
        plugin.getCommand("emsounds")!!.setExecutor(this)
        plugin.getCommand("emreplace")!!.setExecutor(this)
        plugin.getCommand("emrepair")!!.setExecutor(this)
        plugin.getCommand("emsplash")!!.setExecutor(this)
        plugin.getCommand("emnbt")!!.setExecutor(this)
        plugin.getCommand("emojis")!!.setExecutor(this)
        plugin.getCommand("emp")!!.setExecutor(this)
        plugin.getCommand("empireitems")!!.setExecutor(this)
        plugin.getCommand("ezip")!!.setExecutor(this)
        plugin.getCommand("empack")!!.setExecutor(this)
        plugin.getCommand("sit")!!.setExecutor(this)
    }


    private fun empGive(sender: CommandSender, playerName: String, item: String, count: Int) {
        if (plugin.empireItems.empireItems.containsKey(item)) {
            val player: Player = Bukkit.getPlayer(playerName) ?: return
            player.sendMessage(plugin.translations.ITEM_GAINED + "$count $item")
            sender.sendMessage(plugin.translations.ITEM_GIVE + " $item:$count -> $playerName")
            val itemStack = plugin.empireItems.empireItems[item]?.clone() ?: return
            itemStack.amount = count
            player.inventory.addItem(itemStack)
        } else
            sender.sendMessage(plugin.translations.ITEM_NOT_FOUND + item)

    }


    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (sender.hasPermission(EmpirePermissions.EMPGIVE) && (label.equals("emp", true)))
            if (args.size >= 3 && args[0].equals("give", ignoreCase = true)) {
                try {
                    val count: Int = if (args.size > 3) args[3].toInt() else 1
                    empGive(sender, args[1], args[2], count)
                } catch (e: NumberFormatException) {
                    sender.sendMessage(plugin.translations.WRONG_NUMBER)
                }
            }

        if (label.equals("sit", ignoreCase = true)) {
            if (sender is Player)
                SitEvent.instance.sitPlayer(sender as Player)
        }
        if (label.equals("emgui", ignoreCase = true)) {
            EmpireCategoriesMenu(PlayerMenuUtility(sender as Player)).open()
        }
        if (label.equals("emsounds", ignoreCase = true)) {
            EmpireSoundsMenu(PlayerMenuUtility(sender as Player)).open()
        }
        if (label.equals("empack", ignoreCase = true)) {
            if (sender is Player) {

                sender.setResourcePack(plugin.config.resourcePackRef)
            }
        }
        if (label.equals("emreplace", ignoreCase = true)) {
            fun emreplace(sender: Player): Boolean {
                var item = sender.inventory.itemInMainHand
                val meta = item.itemMeta ?: return false
                val id = meta.persistentDataContainer.get(plugin.empireConstants.empireID, PersistentDataType.STRING)
                    ?: return false

                val amount = item.amount
                val durability = item.durability
                item = plugin.empireItems.empireItems[id]!!
                val newMeta = item.itemMeta ?: return true

                for (ench in meta.enchants)
                    newMeta.addEnchant(ench.key, ench.value, true)


                item.itemMeta = newMeta
                item.amount = amount
                item.durability = durability
                sender.inventory.setItemInMainHand(item)
                return true
            }
            if (sender is Player) {
                if (emreplace(sender))
                    sender.sendMessage(plugin.translations.ITEM_REPLACED)
                else
                    sender.sendMessage(plugin.translations.ITEM_REPLACE_WRONG)

            }
        }
        if (label.equals("emojis", ignoreCase = true)) {

            if (sender is Player) {


                var list = ""
                for (emoji in plugin.empireFontImages.fontsInfo.keys) {
                    if (plugin.empireFontImages.fontsInfo[emoji]?.sendBlocked ?: continue)
                        continue
                    list += EmpireUtils.HEXPattern("&r${emoji}\n&r&f${plugin.empireFontImages.fontsInfo[emoji]!!.chars}&r\n")
                }

                val book = EmpireUtils.getBook("RomaRoman", EmpireUtils.HEXPattern("&fЭмодзи"), mutableListOf(list),false)


//                var list = ""
//                val pages: MutableList<String> = mutableListOf()
//                var count = 0
//                for (emoji in plugin.empireFontImages.fontsInfo.keys) {
//                    if (plugin.empireFontImages.fontsInfo[emoji]?.sendBlocked ?: continue)
//                        continue
//
//                    count++
//                    list += "$emoji&r\n&f${plugin.empireFontImages.fontsInfo[emoji]}&r\n"
//                    if (count % 7 == 0) {
//                        pages.add(list)
//                        list = ""
//                    }
//                }
//                pages.add(list)
                //sender.inventory.addItem(book)
                sender.openBook(book)
            }
        }


        if (label.equals("ezip", ignoreCase = true) && sender.hasPermission(EmpirePermissions.EZIP)) {
            sender.sendMessage(plugin.translations.ZIP_START)
            ResourcePack()
            if (ResourcePack.zipAll(
                    plugin.dataFolder.toString() + File.separator + "pack",
                    plugin.dataFolder.toString() + File.separator + "pack" + File.separator + "pack.zip"
                )
            ) {


                sender.sendMessage(plugin.translations.ZIP_SUCCESS)
            } else
                sender.sendMessage(plugin.translations.ZIP_ERROR)

        }

        if (label.equals("ereload", ignoreCase = true) && sender.hasPermission(EmpirePermissions.RELOAD)) {
            sender.sendMessage(plugin.translations.RELOAD)
            plugin.disablePlugin()
            plugin.initPlugin()
            sender.sendMessage(plugin.translations.RELOAD_COMPLETE)
        }
        return false
    }
}