package com.astrainteractive.empireprojekt

import com.astrainteractive.astralibs.AstraLibs
import com.astrainteractive.astralibs.Logger
import com.astrainteractive.empireprojekt.credit.EmpireCredit
import com.astrainteractive.empireprojekt.empire_items.commands.CommandManager
import com.astrainteractive.empireprojekt.empire_items.events.GenericListener
import com.astrainteractive.empireprojekt.empire_items.events.blocks.MushroomBlockEventHandler
import com.astrainteractive.empireprojekt.empire_items.events.genericevents.drop.ItemDropManager
import com.astrainteractive.empireprojekt.empire_items.events.upgrades.UpgradesManager
import com.astrainteractive.empireprojekt.empire_items.items.EmpireItems
import com.astrainteractive.empireprojekt.empire_items.util.*
import com.astrainteractive.empireprojekt.empire_items.util.crafting.CraftingManager
import com.astrainteractive.empireprojekt.empire_items.util.sounds.SoundManager
import com.astrainteractive.empireprojekt.essentials.homes.EssentialsHandler
import com.astrainteractive.empireprojekt.npc.NPCManager
import com.astrainteractive.empireprojekt.empire_items.emgui.data.Category
import com.astrainteractive.empireprojekt.empire_items.emgui.data.Settings
import makeevrserg.empireprojekt.random_items.RandomItems
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.inventory.FurnaceRecipe
import org.bukkit.inventory.Recipe
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.ShapelessRecipe
import org.bukkit.persistence.PersistentDataType
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
         *Items instance
         */
        lateinit var empireItems: EmpireItems
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
         *Font files instance aka custom hud and ui
         */
        lateinit var empireFonts: EmpireFonts
            private set

        /**
         * Drop manager
         */
        lateinit var dropManager: ItemDropManager
            private set


        /**
         *Custom sounds instance
         */
        lateinit var empireSounds: SoundManager
            private set


        /**
         *Npc manager instance
         */
        var npcManager: NPCManager? = null
            private set


        /**
         * Upgrades for items
         */
        lateinit var upgradeManager: UpgradesManager
            private set
    }


    /**
     * Custom crafts
     */
    lateinit var empireCraftingManager: CraftingManager


    /**
     * Command manager for plugin
     */
    private lateinit var commandManager: CommandManager




    /**
     * Handler for new home and warp mechanic
     */
    private lateinit var essentialsHomesHandler: EssentialsHandler


    /**
     * Generic listener event handler
     */
    private lateinit var genericListener: GenericListener

    /**
     * Crafting recipies instance for emgui
     * @see com.astrainteractive.empireprojekt.empire_items.emgui.EmpireCraftMenu
     */
    val recipies: MutableMap<String, CraftingManager.EmpireRecipe>
        get() = empireCraftingManager.empireRecipies

    /**
     * Event handler for custom blocks
     */
    private lateinit var mushroomBlockEventHandler: MushroomBlockEventHandler




    /**
     * Manager for random items
     */
    lateinit var randomItems: RandomItems

    /**
     * Instace of gui Settings
     */
    lateinit var guiSettings: Settings

    /**
     * Instance of gui Categories
     */
    lateinit var guiCategories: Map<String, Category>


    /**
     * Instance for bank/credit system
     */
    lateinit var empireCredit: EmpireCredit

//    private lateinit var database:EmpireDatabase
//    private lateinit var empireRating:EmpireRating

    fun initPlugin() {

        translations = Translations()
        empireFiles = Files()


        empireConfig = EmpireConfig.new()
        empireSounds = SoundManager()
        empireFonts = EmpireFonts.new()
        empireItems = EmpireItems()
        upgradeManager = UpgradesManager()
        genericListener = GenericListener()
        commandManager = CommandManager()
        essentialsHomesHandler = EssentialsHandler()
        dropManager = ItemDropManager()


        randomItems = RandomItems()
        mushroomBlockEventHandler = MushroomBlockEventHandler()

//        _empireCrafts = EmpireCrafts()


        guiCategories = Category.toMap(Category.newCategories() ?: mutableListOf())
        guiSettings = Settings.new()

        empireCredit = EmpireCredit()

        empireCraftingManager = CraftingManager()

        npcManager = NPCManager()
//        database = EmpireDatabase()
//        empireRating = EmpireRating()



        //Beta plugin countdown
        //PluginBetaAccessCheck()

    }




    /**
     * This function called when server starts
     */
    override fun onEnable() {
        instance = this
        AstraLibs.create(this)
        Logger.init("AstraTemplate")
        initPlugin()
    }

    fun disablePlugin() {

        fun isCustomRecipe(key: NamespacedKey) = key.key.contains(BetterConstants.CUSTOM_RECIPE_KEY.name)


        fun isCustomRecipe(recipe: FurnaceRecipe) = isCustomRecipe(recipe.key)


        fun isCustomRecipe(recipe: ShapedRecipe) = isCustomRecipe(recipe.key)

        fun isCustomRecipe(recipe: ShapelessRecipe) = isCustomRecipe(recipe.key)


        fun isCustomRecipe(recipe: Recipe): Boolean {
            return when (recipe) {
                is FurnaceRecipe -> isCustomRecipe(recipe)
                is ShapedRecipe -> isCustomRecipe(recipe)
                is ShapelessRecipe -> isCustomRecipe(recipe)
                else -> false
            }
        }

        genericListener.onDisable()
        mushroomBlockEventHandler.onDisable()
        empireCredit.onDisable()

        npcManager!!.onDisable()
        server.scheduler.cancelTasks(this)
        val ite = server.recipeIterator()
        var recipe: Recipe?
        while (ite.hasNext()) {
            recipe = ite.next()
            if (isCustomRecipe(recipe)) {
                ite.remove()
                continue
            }
            val itemMeta = recipe?.result?.itemMeta ?: continue
            val id = itemMeta.persistentDataContainer.get(
                BetterConstants.EMPIRE_ID.value,
                PersistentDataType.STRING
            ) ?: continue
            if (empireCraftingManager.empireRecipies.contains(id)) ite.remove()
        }
        clearAllTasks()


    }
    /**
     * This function called when server stops
     */
    override fun onDisable() {
        disablePlugin()
        for (p in server.onlinePlayers)
            p.closeInventory()

        Bukkit.getScheduler().cancelTasks(this)

    }
}