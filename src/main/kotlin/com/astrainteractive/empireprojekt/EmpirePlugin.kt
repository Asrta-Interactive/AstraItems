package com.astrainteractive.empireprojekt

import com.astrainteractive.empireprojekt.empire_items.api.drop.DropManager
import com.astrainteractive.astralibs.AstraLibs
import com.astrainteractive.astralibs.Logger
import com.astrainteractive.empireprojekt.credit.EmpireCredit
import com.astrainteractive.empireprojekt.empire_items.api.crafting.CraftingManager
import com.astrainteractive.empireprojekt.empire_items.api.font.FontManager
import com.astrainteractive.empireprojekt.empire_items.api.items.data.ItemManager
import com.astrainteractive.empireprojekt.empire_items.api.upgrade.UpgradeManager
import com.astrainteractive.empireprojekt.empire_items.api.v_trades.VillagerTradeManager
import com.astrainteractive.empireprojekt.empire_items.commands.CommandManager
import com.astrainteractive.empireprojekt.empire_items.events.GenericListener
import com.astrainteractive.empireprojekt.empire_items.util.*
import com.astrainteractive.empireprojekt.npc.NPCManager
import org.bukkit.Bukkit
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask


class EmpirePlugin : JavaPlugin() {

    companion object {
        private val activeTasksList = mutableMapOf<Long,BukkitTask>()

        /**
         * Добавляем ссылка на таск
         */
        fun onBukkitTaskAdded(id: Long, taskRef: BukkitTask) {
            activeTasksList[id] = taskRef
        }

        /**
         * Отключаем таск и выкидываем его из списка
         */
        fun onBukkitTaskEnded(id: Long) {
            val task = activeTasksList[id]
            task?.cancel()
            activeTasksList.remove(id)
        }
        fun clearAllTasks(){
            for ((key,task) in activeTasksList)
                task.cancel()
            activeTasksList.clear()
            for (task in Bukkit.getScheduler().pendingTasks)
                task.cancel()
            for(worker in Bukkit.getScheduler().activeWorkers) try{
                worker.thread.stop()
            }catch (e:Exception){}

        }

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


        /**
         *Config Instance
         */
        lateinit var empireConfig: EmpireConfig
            private set


        /**
         *Npc manager instance
         */
        var npcManager: NPCManager? = null
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





    /**
     * This function called when server starts
     */
    override fun onEnable() {
        instance = this
        AstraLibs.create(this)
        Logger.init("AstraTemplate")
        translations = Translations()
        empireFiles = Files()
        empireConfig = EmpireConfig.new()
        genericListener = GenericListener()
        commandManager = CommandManager()
        empireCredit = EmpireCredit()

        ItemManager.loadItems()
        DropManager.loadDrops()
        VillagerTradeManager.load()
        UpgradeManager.loadUpgrade()
        FontManager.load()
        CraftingManager.load()

    }

    /**
     * This function called when server stops
     */
    override fun onDisable() {
        clearAllTasks()
        genericListener.onDisable()
        for (p in server.onlinePlayers)
            p.closeInventory()
        ItemManager.clear()
        DropManager.clear()
        VillagerTradeManager.clear()
        FontManager.clear()
        CraftingManager.clear()
        HandlerList.unregisterAll()
        Bukkit.getScheduler().cancelTasks(this)

    }
}