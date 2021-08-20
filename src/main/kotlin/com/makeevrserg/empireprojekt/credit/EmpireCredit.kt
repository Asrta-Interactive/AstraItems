package com.makeevrserg.empireprojekt.credit

import com.earth2me.essentials.Essentials
import com.makeevrserg.empireprojekt.credit.commands.CommandManager
import com.makeevrserg.empireprojekt.credit.data.CreditConfig
import com.makeevrserg.empireprojekt.empirelibs.FileManager
import org.bukkit.Bukkit


class EmpireCredit {
    companion object {
        lateinit var instance: EmpireCredit
            private set
        lateinit var configFile: FileManager
            private set
        lateinit var essentials: Essentials
            private set
        lateinit var config: CreditConfig
            private set
    }

    private lateinit var placeholderHook:PlaceholderHook

    private fun initCreditSystem() {
        essentials = (Bukkit.getPluginManager().getPlugin("Essentials") ?: return) as Essentials
        instance = this
        configFile = FileManager("credit/credit.yml")
        config = CreditConfig.new()!!
        CommandManager()
        if (Bukkit.getServer().pluginManager.getPlugin("PlaceholderAPI")!=null) {
            placeholderHook = PlaceholderHook()
            placeholderHook.register()
        }

    }

    init {
        initCreditSystem()
    }
    fun onDisable(){
        if (Bukkit.getServer().pluginManager.getPlugin("PlaceholderAPI")!=null) {
            placeholderHook.unregister()
        }
    }


}