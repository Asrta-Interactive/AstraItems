package com.astrainteractive.empire_items

import com.astrainteractive.astralibs.*
import com.astrainteractive.empire_items.credit.EmpireCredit
import com.astrainteractive.empire_items.api.EmpireAPI
import com.astrainteractive.empire_items.empire_items.commands.CommandManager
import com.astrainteractive.empire_items.empire_items.events.GenericListener
import com.astrainteractive.empire_items.empire_items.util.Config
import com.astrainteractive.empire_items.empire_items.util.Files
import com.astrainteractive.empire_items.empire_items.util.Translations
import com.astrainteractive.empire_items.empire_items.util.protection.KProtectionLib
import com.astrainteractive.empire_items.modules.ModuleManager
import org.bukkit.Bukkit
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin


class EmpirePlugin : JavaPlugin() {

    companion object {

        /**
         *Plugin instance
         */
        lateinit var instance: EmpirePlugin
            private set


        /**
         *Files instance
         */
        lateinit var empireFiles: Files
            private set


        /**
         *Translations instance
         */
        lateinit var translations: Translations
            private set

    }

    /**
     * Command manager for plugin
     */
    private lateinit var commandManager: CommandManager


    /**
     * Generic listener event handler
     */
    private lateinit var genericListener: GenericListener

    /**
     * Instance for bank/credit system
     */
    lateinit var empireCredit: EmpireCredit
    private val licenceTimer = LicenceChecker()






    /**
     * This function called when server starts
     */
    override fun onEnable() {
        instance = this
        AstraLibs.create(this)
        Logger.init("EmpireItems")
        translations = Translations()
        empireFiles = Files()
        Config.load()
        commandManager = CommandManager()
        empireCredit = EmpireCredit()
        ModuleManager.onEnable()
        EmpireAPI.onEnable()
        genericListener = GenericListener()
        if (server.pluginManager.getPlugin("WorldGuard") != null)
            KProtectionLib.init(this)
        licenceTimer.enable()

    }


    /**
     * This function called when server stops
     */
    override fun onDisable() {
        licenceTimer.onDisable()
        AstraLibs.clearAllTasks()
        genericListener.onDisable()
        for (p in server.onlinePlayers)
            p.closeInventory()
        HandlerList.unregisterAll(this)
        Bukkit.getScheduler().cancelTasks(this)
        EmpireAPI.onDisable()
        ModuleManager.onDisable()

    }
}