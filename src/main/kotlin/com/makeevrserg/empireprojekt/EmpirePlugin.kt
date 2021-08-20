package com.makeevrserg.empireprojekt

import com.makeevrserg.empireprojekt.betternpcs.BetterNPCManager
import com.makeevrserg.empireprojekt.credit.EmpireCredit
import com.makeevrserg.empireprojekt.essentials.homes.EssentialsHandler

import com.makeevrserg.empireprojekt.empire_items.commands.CommandManager
import com.makeevrserg.empireprojekt.empire_items.util.EmpireCrafts
import com.makeevrserg.empireprojekt.empire_items.events.GenericListener
import com.makeevrserg.empireprojekt.empire_items.events.genericevents.drop.ItemDropManager
import com.makeevrserg.empireprojekt.empire_items.events.mobs.EmpireMobsManager
import com.makeevrserg.empireprojekt.empire_items.events.upgrades.UpgradesManager
import com.makeevrserg.empireprojekt.empire_items.items.EmpireItems
import com.makeevrserg.empireprojekt.essentials.inventorysaver.ISCommandManager
import com.makeevrserg.empireprojekt.empire_items.events.blocks.MushroomBlockEventHandler
import com.makeevrserg.empireprojekt.empire_items.events.decorations.DecorationBlockEventHandler
import com.makeevrserg.empireprojekt.empire_items.util.*
import com.makeevrserg.empireprojekt.empire_items.util.Files
import com.makeevrserg.empireprojekt.empire_items.util.sounds.SoundManager
import makeevrserg.empireprojekt.emgui.data.Category
import makeevrserg.empireprojekt.emgui.data.Settings
import makeevrserg.empireprojekt.random_items.RandomItems
import org.bukkit.NamespacedKey
import org.bukkit.inventory.FurnaceRecipe
import org.bukkit.inventory.Recipe
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.persistence.PersistentDataType
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
         *Items instance
         */
        lateinit var empireItems: EmpireItems
            private set


        /**
         *Items instance
         */
        lateinit var empireMobs: EmpireMobsManager
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


//        /**
//         *Npc manager instance
//         */
//        var npcManager: NPCManager? = null
//            private set


        lateinit var betterNPCManager:BetterNPCManager
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
    lateinit var _empireCrafts: EmpireCrafts


    /**
     * Command manager for plugin
     */
    private lateinit var commandManager: CommandManager


    /**
     * Command manager for item saving
     */
    private lateinit var isCommandManager: ISCommandManager


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
     * @see com.makeevrserg.empireprojekt.empire_items.emgui.EmpireCraftMenu
     */
    val recipies: MutableMap<String, EmpireCrafts.EmpireRecipe>
        get() = _empireCrafts.empireRecipies

    /**
     * Event handler for custom blocks
     */
    private lateinit var mushroomBlockEventHandler: MushroomBlockEventHandler

    /**
     * Event handler for decoration blocks
     */
    private lateinit var decorationBlockEventHandler: DecorationBlockEventHandler


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


    fun initPlugin() {

        translations = Translations()
        empireFiles = Files()


        empireConfig = EmpireConfig.new()
        empireSounds = SoundManager()
        empireFonts = EmpireFonts.new()
        empireItems = EmpireItems()
        empireMobs = EmpireMobsManager()
        upgradeManager = UpgradesManager()
        genericListener = GenericListener()
        commandManager = CommandManager()
        essentialsHomesHandler = EssentialsHandler()
        dropManager = ItemDropManager()


        randomItems = RandomItems()
        mushroomBlockEventHandler = MushroomBlockEventHandler()
        decorationBlockEventHandler = DecorationBlockEventHandler()
        isCommandManager = ISCommandManager()
        _empireCrafts = EmpireCrafts()


        guiCategories = Category.toMap(Category.newCategories() ?: mutableListOf())
        guiSettings = Settings.new()

        empireCredit = EmpireCredit()

//        if (server.pluginManager.getPlugin("ProtocolLib") != null)
//            npcManager = NPCManager()
//        else
//            println(translations.PLUGIN_PROTOCOLLIB_NOT_INSTALLED)


        betterNPCManager = BetterNPCManager()
        //Beta plugin countdown
        //PluginBetaAccessCheck()

    }


    /**
     * This function called when server starts
     */
    override fun onEnable() {
        instance = this
        initPlugin()
    }

    fun disablePlugin() {

        fun isCustomRecipe(key: NamespacedKey): Boolean {
            return key.key.contains(BetterConstants.CUSTOM_RECIPE_KEY.name)
        }

        fun isCustomRecipe(recipe: FurnaceRecipe): Boolean {
            return isCustomRecipe(recipe.key)
        }

        fun isCustomRecipe(recipe: ShapedRecipe): Boolean {
            return isCustomRecipe(recipe.key)
        }

        fun isCustomRecipe(recipe: Recipe): Boolean {
            if (recipe is FurnaceRecipe)
                return isCustomRecipe(recipe)
            else if (recipe is ShapedRecipe)
                return isCustomRecipe(recipe)
            return false
        }

        betterNPCManager.onDisable()

        empireCredit.onDisable()
//        if (npcManager != null)
//            npcManager!!.onDisable()

        genericListener.onDisable()
        mushroomBlockEventHandler.onDisable()
        empireMobs.onDisable()
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
            if (_empireCrafts.empireRecipies.contains(id)) ite.remove()
        }


    }
    /**
     * This function called when server stops
     */
    override fun onDisable() {
        disablePlugin()
        for (p in server.onlinePlayers)
            p.closeInventory()

    }
}