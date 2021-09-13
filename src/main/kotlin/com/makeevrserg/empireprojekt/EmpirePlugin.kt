package com.makeevrserg.empireprojekt

import com.makeevrserg.empireprojekt.credit.EmpireCredit
import com.makeevrserg.empireprojekt.empire_items.commands.CommandManager
import com.makeevrserg.empireprojekt.empire_items.events.GenericListener
import com.makeevrserg.empireprojekt.empire_items.events.blocks.MushroomBlockEventHandler
import com.makeevrserg.empireprojekt.empire_items.events.decorations.DecorationBlockEventHandler
import com.makeevrserg.empireprojekt.empire_items.events.genericevents.drop.ItemDropManager
import com.makeevrserg.empireprojekt.empire_items.events.upgrades.UpgradesManager
import com.makeevrserg.empireprojekt.empire_items.items.EmpireItems
import com.makeevrserg.empireprojekt.empire_items.util.*
import com.makeevrserg.empireprojekt.empire_items.util.crafting.CraftingManager
import com.makeevrserg.empireprojekt.empire_items.util.sounds.SoundManager
import com.makeevrserg.empireprojekt.empirelibs.PluginBetaAccessCheck
import com.makeevrserg.empireprojekt.empirelibs.database.EmpireDatabase
import com.makeevrserg.empireprojekt.essentials.homes.EssentialsHandler
import com.makeevrserg.empireprojekt.npc.NPCManager
import makeevrserg.empireprojekt.emgui.data.Category
import makeevrserg.empireprojekt.emgui.data.Settings
import makeevrserg.empireprojekt.random_items.RandomItems
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.inventory.FurnaceRecipe
import org.bukkit.inventory.Recipe
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.ShapelessRecipe
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
     * @see com.makeevrserg.empireprojekt.empire_items.emgui.EmpireCraftMenu
     */
    val recipies: MutableMap<String, CraftingManager.EmpireRecipe>
        get() = empireCraftingManager.empireRecipies

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
        decorationBlockEventHandler = DecorationBlockEventHandler()
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


//        database.onDisable()
//        empireRating.onDisable()

        empireCredit.onDisable()

        npcManager!!.onDisable()
        genericListener.onDisable()
        mushroomBlockEventHandler.onDisable()
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