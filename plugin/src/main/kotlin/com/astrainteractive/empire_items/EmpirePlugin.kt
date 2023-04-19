package com.astrainteractive.empire_items

import com.astrainteractive.empire_items.di.*
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren
import org.bukkit.Bukkit
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.astralibs.AstraLibs
import ru.astrainteractive.astralibs.Logger
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.events.GlobalEventManager
import ru.astrainteractive.astralibs.menu.SharedInventoryClickEvent


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
        TranslationModule.value
        enchantsConfigModule.value
        GuiConfigModule.value
        configModule.value
        empireItemsApiModule.value
        fontApiModule.value
        empireUtilsModule.value
        craftingControllerModule.apply {
            value.create()
            value
        }
        bossBarControllerModule.value
        empireModelEngineApiModule.value
        commandManagerModule.value
        enchantMangerModule.value
        genericListenerModule.value
        SharedInventoryClickEvent.onEnable(GlobalEventManager)
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
        PluginScope.close()
        BlockGenerationDispatchers.blockGenerationPool.cancel()
        BlockGenerationDispatchers.blockParsingPool.cancel()
        BlockGenerationDispatchers.fileHistoryScope.cancel()
    }

    fun reload(){
        TranslationModule.reload()
        enchantsConfigModule.reload()
        GuiConfigModule.reload()
        configModule.reload()
        empireItemsApiModule.reload()
        empireModelEngineApiModule.reload()
        enchantMangerModule.reload()
        genericListenerModule.reload()

        craftingControllerModule.apply {
            value.clear()
            value.create()
            value
        }
        for (p in server.onlinePlayers)
            p.closeInventory()
//        Bukkit.getScheduler().cancelTasks(this)
        PluginScope.cancelChildren()
        BlockGenerationDispatchers.blockGenerationPool.cancelChildren()
        BlockGenerationDispatchers.blockParsingPool.cancelChildren()

    }
}