package com.makeevrserg.empireprojekt

import com.makeevrserg.empireprojekt.ESSENTIALS.homes.EssentialsHandler
import com.makeevrserg.empireprojekt.NPCS.NPCManager
import com.makeevrserg.empireprojekt.commands.CommandManager
import com.makeevrserg.empireprojekt.util.CraftEvent
import com.makeevrserg.empireprojekt.events.GenericListener
import com.makeevrserg.empireprojekt.events.ItemUpgradeEvent
import com.makeevrserg.empireprojekt.events.genericevents.ItemDropListener
import com.makeevrserg.empireprojekt.items.EmpireItems
import com.makeevrserg.empireprojekt.menumanager.menu.CategoryItems
import com.makeevrserg.empireprojekt.util.*
import com.makeevrserg.empireprojekt.util.files.Files
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.NamespacedKey
import org.bukkit.inventory.FurnaceRecipe
import org.bukkit.inventory.Recipe
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import java.text.SimpleDateFormat
import java.util.*


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
        //Translations instance
        lateinit var translations: Translations
            private set
        //Config Instance
        lateinit var config: EmpireConfig
            private set
        //Font files instance aka custom hud and ui
        lateinit var empireFontImages: EmpireFontImages
            private set
        //Constants
        lateinit var empireConstants: EmpireConstats
            private set
        //Custom sounds instance
        lateinit var empireSounds: EmpireSounds
            private set
        //Npc manager instance
        var npcManager: NPCManager? = null
            private set
    }


    var _craftEvent = CraftEvent()
    //Command manager for plugin
    private lateinit var commandManager: CommandManager
    //Handler for new home and warp mechanic
    private lateinit var essentialsHomesHandler: EssentialsHandler

    //private lateinit var npcManager:NPCManager
    private lateinit var genericListener: GenericListener
    //Category section form emgui
    lateinit var categoryItems: MutableMap<String, CategoryItems.CategorySection>
    //Drop from item
    lateinit var getEveryDrop: MutableMap<String, MutableList<ItemDropListener.ItemDrop>>

    //Recipies for items
    val recipies: MutableMap<String, CraftEvent.EmpireRecipe>
        get() = _craftEvent.empireRecipies
    //Upgrades for items
    val upgradesMap: MutableMap<String, List<ItemUpgradeEvent.ItemUpgrade>>
        get() = genericListener._itemUpgradeEvent.upgradesMap

    fun initPlugin() {
        empireConstants = EmpireConstats()
        translations = Translations()
        empireFiles = Files()
        EmpirePlugin.config =
            EmpireConfig(empireFiles.configFile.getConfig())
        empireSounds = EmpireSounds()
        empireFontImages =
            EmpireFontImages(empireFiles.fontImagesFile.getConfig())
        empireItems = EmpireItems()
        genericListener = GenericListener()
        commandManager = CommandManager()
        essentialsHomesHandler = EssentialsHandler()
        //npcManager = NPCManager()
        categoryItems =
            CategoryItems(
                empireFiles.guiFile.getConfig()?.getConfigurationSection("categories")
            ).categoriesMap
        getEveryDrop = genericListener._itemDropListener.everyDropByItem


        empireSounds.getSounds()

        if (server.pluginManager.getPlugin("ProtocolLib") != null) {
            npcManager = NPCManager()
        } else
            println(translations.PLUGIN_PROTOCOLLIB_NOT_INSTALLED)

        //Beta plugin countdown
        //PluginBetaAccessCheck()

    }

    class PluginBetaAccessCheck() {
        init {
            initBetaPluginChecker()
        }

        fun initBetaPluginChecker() {
            Bukkit.getScheduler().runTaskTimer(EmpirePlugin.instance, Runnable { checkTime() }, 0, 500)
        }

        fun getDate(milliSeconds: Long, dateFormat: String?): String {
            // Create a DateFormatter object for displaying date in specified format.
            val formatter = SimpleDateFormat(dateFormat)

            // Create a calendar object that will convert the date and time value in milliseconds to date.
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = milliSeconds
            return formatter.format(calendar.time)
        }

        fun checkTime(): Boolean {
            fun hoursToMS(h: Int): Long {
                return h * 60 * 60 * 1000L
            }

            fun minuteToMS(m: Int): Long {
                return m * 60 * 1000L
            }

            val maxTime: Long = hoursToMS(170)// * 60 * 1000
            val time: Long = 1626024523239
            val date = getDate(time + maxTime, "dd/MM/yyyy HH:mm:ss")
            EmpirePlugin.instance.server.broadcastMessage("${ChatColor.RED}Используется тестовая версия плагина EmpireItems.")
            EmpirePlugin.instance.server.broadcastMessage("${ChatColor.RED}Плагин будет отключен $date")
            EmpirePlugin.instance.server.broadcastMessage("${ChatColor.RED}EmpireProjekt.ru")

            if (System.currentTimeMillis() - time < maxTime)
                println(
                    "${ChatColor.RED}" +
                            "Вы используете тестовую версию! Плагин отключится " +
                            date
                )
            else {
                println("${ChatColor.RED}Тестовая версия плагина закончилась! Зайтите в дискорд EmpireProjekt.ru/discord и попросите новую версию")
                EmpirePlugin.instance.server.broadcastMessage("${ChatColor.RED}Тестовая версия плагина закончилась! Зайтите в дискорд EmpireProjekt.ru/discord и попросите новую версию")
                EmpirePlugin.instance.onDisable()
                return false
            }
            return true

        }
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
        //npcManager.onDisable()
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
            if (_craftEvent.empireRecipies.contains(id)) ite.remove()
        }


    }

    override fun onDisable() {
        disablePlugin()
        for (p in server.onlinePlayers)
            p.closeInventory()

    }
}