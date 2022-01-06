package com.astrainteractive.empire_items

import com.astrainteractive.astralibs.*
import com.astrainteractive.empire_items.credit.EmpireCredit
import com.astrainteractive.empire_items.empire_items.api.crafting.CraftingManager
import com.astrainteractive.empire_items.empire_items.api.drop.DropManager
import com.astrainteractive.empire_items.empire_items.api.font.FontManager
import com.astrainteractive.empire_items.empire_items.api.items.data.ItemManager
import com.astrainteractive.empire_items.empire_items.api.upgrade.UpgradeManager
import com.astrainteractive.empire_items.empire_items.api.v_trades.VillagerTradeManager
import com.astrainteractive.empire_items.empire_items.commands.CommandManager
import com.astrainteractive.empire_items.empire_items.events.GenericListener
import com.astrainteractive.empire_items.empire_items.util.Config
import com.astrainteractive.empire_items.empire_items.util.Files
import com.astrainteractive.empire_items.empire_items.util.Translations
import com.astrainteractive.empire_items.empire_items.util.protection.KProtectionLib
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

//    private lateinit var database:EmpireDatabase
//    private lateinit var empireRating:EmpireRating
    private val licenceTimer = LicenceChecker()

    /**
     * This function called when server starts
     */
    override fun onEnable() {
        instance = this
        AstraLibs.create(this)
        Logger.init("AstraTemplate")
        FontManager.load()
        translations = Translations()
        empireFiles = Files()
        Config.load()
        genericListener = GenericListener()
        commandManager = CommandManager()
        empireCredit = EmpireCredit()
        ItemManager.loadItems()
        DropManager.loadDrops()
        VillagerTradeManager.load()
        UpgradeManager.loadUpgrade()
        CraftingManager.load()
        if (server.pluginManager.getPlugin("WorldGuard")!=null)
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
        ItemManager.clear()
        DropManager.clear()
        VillagerTradeManager.clear()
        FontManager.clear()
        CraftingManager.clear()
        HandlerList.unregisterAll(this)
        Bukkit.getScheduler().cancelTasks(this)

    }
}