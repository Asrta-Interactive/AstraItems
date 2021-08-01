package com.makeevrserg.empireprojekt

import com.makeevrserg.empireprojekt.essentials.homes.EssentialsHandler
import com.makeevrserg.empireprojekt.npcs.NPCManager
import com.makeevrserg.empireprojekt.commands.CommandManager
import com.makeevrserg.empireprojekt.util.EmpireCrafts
import com.makeevrserg.empireprojekt.events.GenericListener
import com.makeevrserg.empireprojekt.events.genericevents.drop.ItemDropManager
import com.makeevrserg.empireprojekt.events.mobs.EmpireMobsManager
import com.makeevrserg.empireprojekt.events.upgrades.UpgradesManager
import com.makeevrserg.empireprojekt.items.EmpireItems
import com.makeevrserg.empireprojekt.menumanager.emgui.settings.GuiCategories
import com.makeevrserg.empireprojekt.menumanager.emgui.settings.GuiSettings
import com.makeevrserg.empireprojekt.util.*
import com.makeevrserg.empireprojekt.util.Files
import empirelibs.PluginBetaAccessCheck
import org.bukkit.NamespacedKey
import org.bukkit.inventory.FurnaceRecipe
import org.bukkit.inventory.Recipe
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin


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
        lateinit var config: EmpireConfig
            private set

        //Font files instance aka custom hud and ui
        lateinit var empireFonts: EmpireFonts
            private set

        //Constants
        lateinit var empireConstants: EmpireConstats
            private set

        lateinit var dropManager: ItemDropManager
            private set

        //Custom sounds instance
        lateinit var empireSounds: EmpireSounds
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

    //Handler for new home and warp mechanic
    private lateinit var essentialsHomesHandler: EssentialsHandler

    //private lateinit var npcManager:NPCManager
    private lateinit var genericListener: GenericListener

//    //Category section form emgui
//    lateinit var categoryItems: MutableMap<String, CategoryItems.CategorySection>

    //Drop from item
    lateinit var getEveryDrop: MutableMap<String, MutableList<ItemDropManager.ItemDrop>>

    //Recipies for items
    val recipies: MutableMap<String, EmpireCrafts.EmpireRecipe>
        get() = _empireCrafts.empireRecipies



    //GuiSettings
    lateinit var guiSettings: GuiSettings

    //Gui Categories
    lateinit var guiCategories: GuiCategories

    fun initPlugin() {
        empireConstants = EmpireConstats()
        translations = Translations()
        empireFiles = Files()
        EmpirePlugin.config = EmpireConfig.create()
        empireSounds = EmpireSounds()
        empireFonts = EmpireFonts(empireFiles.fontImagesFile.getConfig())
        empireItems = EmpireItems()
        empireMobs = EmpireMobsManager()
        upgradeManager = UpgradesManager()
        genericListener = GenericListener()
        commandManager = CommandManager()
        essentialsHomesHandler = EssentialsHandler()
        dropManager = ItemDropManager()
        getEveryDrop = dropManager.everyDropByItem



        _empireCrafts = EmpireCrafts()
        empireSounds.getSounds()

        guiSettings = GuiSettings()
        guiCategories = GuiCategories()
        if (server.pluginManager.getPlugin("ProtocolLib") != null) {
            npcManager = NPCManager()
        } else
            println(translations.PLUGIN_PROTOCOLLIB_NOT_INSTALLED)

        //Beta plugin countdown
        PluginBetaAccessCheck()

    }


    override fun onEnable() {
        instance = this
        initPlugin()
    }

    fun disablePlugin() {

        fun isCustomRecipe(key: NamespacedKey): Boolean {
            return key.key.contains(empireConstants.CUSTOM_RECIPE_KEY)
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
                empireConstants.empireID,
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