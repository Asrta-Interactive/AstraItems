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
        TranslationModule.reload()
        enchantsConfigModule.reload()
        GuiConfigModule.reload()
        configModule.reload()
        enchantMangerModule.apply {
            value.onDisable()
            reload()
        }
        genericListenerModule.apply {
            value.onDisable()
            reload()
        }
        empireItemsApiModule.reload()
        craftingControllerModule.apply {
            value.clear()
            value.create()
        }
        empireModelEngineApiModule.apply {
            value.clear()
            reload()
        }
        bossBarControllerModule.apply {
            value.reset()
        }
        commandManagerModule.value
        fontApiModule.value
    }


    /**
     * This function called when server stops
     */
    override fun onDisable() {
        enchantMangerModule.value.onDisable()
        genericListenerModule.value.onDisable()
        craftingControllerModule.value.clear()
        empireModelEngineApiModule.value.clear()
        bossBarControllerModule.value.reset()

        for (p in server.onlinePlayers)
            p.closeInventory()
        GlobalEventManager.onDisable()
        HandlerList.unregisterAll(this)
        Bukkit.getScheduler().cancelTasks(this)
        PluginScope.cancel()
    }
}