package com.astrainteractive.empire_items

import ru.astrainteractive.astralibs.AstraLibs
import ru.astrainteractive.astralibs.Logger
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.events.GlobalEventManager
import com.astrainteractive.empire_itemss.api.CraftingApi
import com.astrainteractive.empire_itemss.api.EmpireItemsAPI
import com.astrainteractive.empire_items.commands.CommandManager
import com.astrainteractive.empire_items.di.genericListenerModule
import com.astrainteractive.empire_items.events.GenericListener
import com.astrainteractive.empire_items.meg.BossBarController
import com.astrainteractive.empire_items.meg.api.EmpireModelEngineAPI
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
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
    }
    init {
        instance = this
        AstraLibs.rememberPlugin(this)
    }

    /**
     * This function called when server starts
     */
    override fun onEnable() {
        Logger.prefix = "EmpireItems"
        CommandManager()
        genericListenerModule.value
    }


    /**
     * This function called when server stops
     */
    override fun onDisable() {
        for (p in server.onlinePlayers)
            p.closeInventory()
        genericListenerModule.value.onDisable()
        GlobalEventManager.onDisable()
        HandlerList.unregisterAll(this)
        Bukkit.getScheduler().cancelTasks(this)
        PluginScope.cancel()
    }
}