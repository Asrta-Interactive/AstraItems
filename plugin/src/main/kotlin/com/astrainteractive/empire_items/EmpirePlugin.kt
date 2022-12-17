package com.astrainteractive.empire_items

import ru.astrainteractive.astralibs.AstraLibs
import ru.astrainteractive.astralibs.Logger
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.events.GlobalEventManager
import com.astrainteractive.empire_items.commands.CommandManager
import com.astrainteractive.empire_items.di.*
import kotlinx.coroutines.cancel
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
        craftingControllerModule.value.create()
    }


    /**
     * This function called when server stops
     */
    override fun onDisable() {
        craftingControllerModule.value.clear()

        bossBarControllerModule.value.reset()
        empireModelEngineApiModule.value.clear()

        for (p in server.onlinePlayers)
            p.closeInventory()
        genericListenerModule.value.onDisable()
        GlobalEventManager.onDisable()
        HandlerList.unregisterAll(this)
        Bukkit.getScheduler().cancelTasks(this)
        PluginScope.cancel()

    }

    fun reload(){
        empireItemsApiModule.reload()
        enchantMangerModule.apply {
            value.onDisable()
            reload()
        }
        craftingControllerModule.value.apply {
            clear()
            create()
        }

        bossBarControllerModule.value.reset()
        empireModelEngineApiModule.apply {
            value.clear()
            reload()
        }



    }
}