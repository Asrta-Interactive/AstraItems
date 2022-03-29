package com.astrainteractive.empire_items

import com.astrainteractive.astralibs.AstraLibs
import com.astrainteractive.astralibs.LicenceChecker
import com.astrainteractive.astralibs.Logger
import com.astrainteractive.astralibs.async.AsyncHelper
import com.astrainteractive.empire_items.api.EmpireAPI
import com.astrainteractive.empire_items.credit.EmpireCredit
import com.astrainteractive.empire_items.empire_items.commands.CommandManager
import com.astrainteractive.empire_items.empire_items.events.GenericListener
import com.astrainteractive.empire_items.empire_items.util.Config
import com.astrainteractive.empire_items.empire_items.util.Files
import com.astrainteractive.empire_items.empire_items.util.Translations
import com.astrainteractive.empire_items.empire_items.util.protection.KProtectionLib
import com.astrainteractive.empire_items.modules.ModuleManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import org.bukkit.Bukkit
import org.bukkit.event.HandlerList
import org.bukkit.plugin.PluginDescriptionFile
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.plugin.java.JavaPluginLoader
import java.io.File
import kotlin.coroutines.ContinuationInterceptor




class EmpirePlugin : JavaPlugin {
    constructor() : super() {}
    constructor(
        loader: JavaPluginLoader?,
        description: PluginDescriptionFile?,
        dataFolder: File?,
        file: File?
    ) : super(
        loader!!, description!!, dataFolder!!, file!!
    ) {
    }
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
        lateinit var mainThread: Thread
            private set
        lateinit var mainDispatcher: CoroutineDispatcher
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
        mainDispatcher = runBlocking { coroutineContext[ContinuationInterceptor] as CoroutineDispatcher }
        mainThread = Thread.currentThread()
        instance = this
        AstraLibs.create(this)
        Logger.init("EmpireItems")
        translations = Translations()
        empireFiles = Files()
        Config.load()
        commandManager = CommandManager()
        empireCredit = EmpireCredit()
        runBlocking {
            ModuleManager.onEnable()
            EmpireAPI.onEnable()
        }
//        Timer().calculate {
//
//        }.also {
//            Logger.log("ModuleManager and EmpireAPI time ${it}")
//        }
        genericListener = GenericListener()
        if (server.pluginManager.getPlugin("WorldGuard") != null)
            KProtectionLib.init(this)
//        licenceTimer.enable()

    }


    /**
     * This function called when server stops
     */
    override fun onDisable() {
        AsyncHelper.cancel()
        licenceTimer.onDisable()
        AstraLibs.clearAllTasks()
        genericListener.onDisable()
        for (p in server.onlinePlayers)
            p.closeInventory()
        HandlerList.unregisterAll(this)
        Bukkit.getScheduler().cancelTasks(this)
        runBlocking {
            EmpireAPI.onDisable()
            ModuleManager.onDisable()
        }
        AsyncHelper.cancel()

    }
}