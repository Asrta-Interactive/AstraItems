package com.makeevrserg.empireprojekt

import com.makeevrserg.empireprojekt.essentials.homes.EssentialsHandler
import npcs.NPCManager
import com.makeevrserg.empireprojekt.commands.CommandManager
import com.makeevrserg.empireprojekt.util.EmpireCrafts
import com.makeevrserg.empireprojekt.events.GenericListener
import com.makeevrserg.empireprojekt.events.genericevents.drop.ItemDropManager
import com.makeevrserg.empireprojekt.events.mobs.EmpireMobsManager
import com.makeevrserg.empireprojekt.events.upgrades.UpgradesManager
import com.makeevrserg.empireprojekt.items.EmpireItems
import com.makeevrserg.empireprojekt.essentials.inventorysaver.ISCommandManager
import com.makeevrserg.empireprojekt.events.blocks.MushroomBlockEventHandler
import com.makeevrserg.empireprojekt.events.decorations.DecorationBlockEventHandler
import com.makeevrserg.empireprojekt.util.*
import com.makeevrserg.empireprojekt.util.Files
import com.makeevrserg.empireprojekt.util.sounds.SoundManager
import empirelibs.PluginBetaAccessCheck
import makeevrserg.empireprojekt.emgui.data.Category
import makeevrserg.empireprojekt.emgui.data.Settings
import makeevrserg.empireprojekt.random_items.RandomItems
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarFlag
import org.bukkit.boss.BarStyle
import org.bukkit.inventory.FurnaceRecipe
import org.bukkit.inventory.Recipe
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import shop.EmpireShopManager

class EmpirePlugin : JavaPlugin() {

    companion object {
        //Plugin instance
        lateinit var instance: EmpirePlugin
            private set

        //Files instance
        lateinit var empireFiles: Files
            private set

        //Items instance
        lateinit var empireItems: EmpireItems
            private set

        //Items instance
        lateinit var empireMobs: EmpireMobsManager
            private set

        //Translations instance
        lateinit var translations: Translations
            private set

        //Config Instance
        lateinit var empireConfig: EmpireConfig
            private set

        //Font files instance aka custom hud and ui
        lateinit var empireFonts: EmpireFonts
            private set

        //Constants
//        lateinit var empireConstants: EmpireConstats
//            private set

        lateinit var dropManager: ItemDropManager
            private set

        //Custom sounds instance
        lateinit var empireSounds: SoundManager
            private set

        //Npc manager instance
        var npcManager: NPCManager? = null
            private set

        //Upgrades for items
        lateinit var upgradeManager:UpgradesManager
            private set
    }


    lateinit var _empireCrafts: EmpireCrafts

    //Command manager for plugin
    private lateinit var commandManager: CommandManager


    //Command manager for item saving
    private lateinit var isCommandManager: ISCommandManager

    //Handler for new home and warp mechanic
    private lateinit var essentialsHomesHandler: EssentialsHandler

    //private lateinit var npcManager:NPCManager
    private lateinit var genericListener: GenericListener

    //Recipies for items
    val recipies: MutableMap<String, EmpireCrafts.EmpireRecipe>
        get() = _empireCrafts.empireRecipies

    private lateinit var mushroomBlockEventHandler: MushroomBlockEventHandler
    private lateinit var decorationBlockEventHandler:DecorationBlockEventHandler


    public lateinit var randomItems:RandomItems

    lateinit var guiSettings:Settings
    lateinit var guiCategories:Map<String,Category>


    private lateinit var shopManager:EmpireShopManager

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


        guiCategories = Category.toMap(Category.newCategories()?: mutableListOf())
        guiSettings = Settings.new()

        if (server.pluginManager.getPlugin("ProtocolLib") != null) {
            npcManager = NPCManager()
        } else
            println(translations.PLUGIN_PROTOCOLLIB_NOT_INSTALLED)

        //shopManager = EmpireShopManager()
        //Beta plugin countdown
        //PluginBetaAccessCheck()

    }


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


        if (npcManager != null)
            npcManager!!.onDisable()

        genericListener.onDisable()
        mushroomBlockEventHandler.onDisable()
//        genericListener.onDisable()
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

    override fun onDisable() {
        disablePlugin()
        for (p in server.onlinePlayers)
            p.closeInventory()

    }
}