package com.makeevrserg.empireprojekt.essentials.homes

import com.earth2me.essentials.Essentials
import com.makeevrserg.empireprojekt.EmpirePlugin
import empirelibs.menu.PlayerMenuUtility
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class EssentialsHandler : CommandExecutor {



    private fun initEssentials(){
        ess = (Bukkit.getPluginManager().getPlugin("Essentials")?:return) as Essentials
    }
    init {

        instance = this
        initEssentials()

        EmpirePlugin.instance.getCommand("emess")!!.setExecutor(this)
    }

    companion object {
        lateinit var instance: EssentialsHandler
            private set
        var ess: Essentials? = null
            private set
    }


    public fun onDisable() {
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player)
            return false
        ess ?: return false

        if (args[0].equals("homes", ignoreCase = true))
            HomesMenu(PlayerMenuUtility(sender)).open()
        if (args[0].equals("warps", ignoreCase = true))
            WarpsMenu(PlayerMenuUtility(sender)).open()

        return false
    }
}