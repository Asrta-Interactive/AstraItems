package com.makeevrserg.empireprojekt.essentials.inventorysaver

import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.util.EmpirePermissions
import empirelibs.FileManager
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.io.File

class ISCommandManager : CommandExecutor {

    private var tabCompletion: ISTabCompleter = ISTabCompleter()
    init {
        EmpirePlugin.instance.getCommand("eminv")!!.tabCompleter = tabCompletion
        EmpirePlugin.instance.getCommand("eminv")!!.setExecutor(this)
    }

    companion object{
        public fun getFile(p:Player): FileManager {
            return FileManager("temp" + File.separator + p.uniqueId+".yml")
        }
    }


    private fun saveInv(p:Player,name:String,saveTo:Player?=null){
        val file = getFile(saveTo?:p)
        file.getConfig()?.set("$name",p.inventory.contents)
        file.saveConfig()
        p.sendMessage(EmpirePlugin.translations.INVENTORY_SAVED)
    }
    private fun loadInv(p:Player,name:String){
        val file = getFile(p)
        val list = (file.getConfig()?.getList("$name")?:return) as List<ItemStack?>
        p.inventory.contents = list.toTypedArray()
        p.sendMessage(EmpirePlugin.translations.INVENTORY_LOADED)
    }
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player)
            return false
        if (!sender.hasPermission(EmpirePermissions.INVENTORY_SAVE)) {
            sender.sendMessage(EmpirePlugin.translations.NO_PERMISSION)
            return false
        }
        when (args.size) {
            0 -> return false
            1 ->{
                if (!args[0].equals("save",ignoreCase = true)) {
                    sender.sendMessage(EmpirePlugin.translations.WRONG_ARGS)
                    return false
                }
                sender.sendMessage(EmpirePlugin.translations.WRONG_ARGS)
                return false
            }
            2->{
                when {
                    args[0].equals("save",ignoreCase = true) -> {
                        saveInv(sender,args[1])
                        sender.sendMessage(EmpirePlugin.translations.INVENTORY_SAVED)
                    }
                    args[0].equals("load",ignoreCase = true) -> {
                        saveInv(sender,"${sender.name}_prev")
                        loadInv(sender,args[1])
                    }
                    args[0].equals("delete",ignoreCase = true) -> {
                        val file = getFile(sender)
                        file.getConfig()?.set(args[1],null)?:return false
                        file.saveConfig()
                        sender.sendMessage(EmpirePlugin.translations.SUCCESS)
                    }
                }
            }
            3->{
                if (args[0].equals("save",ignoreCase = true)) {
                    saveInv(Bukkit.getPlayer(args[2])?:return false,args[1],saveTo = sender)
                    sender.sendMessage(EmpirePlugin.translations.INVENTORY_SAVED)
                } else if (args[0].equals("load",ignoreCase = true)){
                    saveInv(Bukkit.getPlayer(args[2])?:return false,"${args[2]}_prev",saveTo = sender)
                    loadInv(Bukkit.getPlayer(args[2])?:return false,args[1])
                }
            }
        }
        return false
    }
}