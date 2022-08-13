package com.astrainteractive.empire_items

import com.astrainteractive.astralibs.AstraLibs
import com.astrainteractive.astralibs.Logger
import com.astrainteractive.astralibs.async.AsyncHelper
import com.astrainteractive.astralibs.events.GlobalEventManager
import com.astrainteractive.empire_items.api.CraftingApi
import com.astrainteractive.empire_items.api.EmpireItemsAPI
import com.astrainteractive.empire_items.empire_items.commands.CommandManager
import com.astrainteractive.empire_items.empire_items.events.GenericListener
import com.astrainteractive.empire_items.empire_items.events.api_events.model_engine.ModelEngineApi
import com.astrainteractive.empire_items.empire_items.util.protection.KProtectionLib
import com.astrainteractive.empire_items.modules.ModuleManager
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import org.bukkit.Bukkit
import org.bukkit.event.HandlerList
import org.bukkit.plugin.PluginDescriptionFile
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.plugin.java.JavaPluginLoader
import java.io.File


class EmpirePlugin : JavaPlugin {
    constructor() : super() {}
    constructor(
        loader: JavaPluginLoader?,
        description: PluginDescriptionFile?,
        dataFolder: File?,
        file: File?,
    ) : super(
        loader!!, description!!, dataFolder!!, file!!
    )

    companion object {
        /**
         *Plugin instance
         */
        lateinit var instance: EmpirePlugin
            private set
    }

    /**
     * Command manager for plugin
     */
    private val commandManager by lazy { CommandManager() }


    /**
     * Generic listener event handler
     */
    private lateinit var genericListener: GenericListener

    private val modules = buildList {
        add(EmpireItemsAPI)
        add(ModuleManager)
        add(CraftingApi)
        add(ModelEngineApi)
    }



    /**
     * This function called when server starts
     */
    override fun onEnable() {
        instance = this
        AstraLibs.rememberPlugin(this)
        Logger.prefix = "EmpireItems"
        ResourceProvider.reload()
        commandManager
        runBlocking { modules.forEach { it.onEnable() } }
        genericListener = GenericListener()
        server.pluginManager.getPlugin("WorldGuard")?.let {
            KProtectionLib.init(this)
        }
    }


    /**
     * This function called when server stops
     */
    override fun onDisable() {
        AsyncHelper.cancel()
        genericListener.onDisable()
        for (p in server.onlinePlayers)
            p.closeInventory()

        GlobalEventManager.onDisable()
        HandlerList.unregisterAll(this)
        Bukkit.getScheduler().cancelTasks(this)
        runBlocking { modules.forEach { it.onDisable() } }
        AsyncHelper.cancel()
    }
}