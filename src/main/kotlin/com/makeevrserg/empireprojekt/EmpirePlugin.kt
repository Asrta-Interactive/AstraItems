package com.makeevrserg.empireprojekt

import com.makeevrserg.empireprojekt.commands.CommandManager
import com.makeevrserg.empireprojekt.events.GenericListener
import com.makeevrserg.empireprojekt.events.genericlisteners.ItemDropListener
import com.makeevrserg.empireprojekt.items.EmpireItems
import com.makeevrserg.empireprojekt.menumanager.menu.CategoryItems
import com.makeevrserg.empireprojekt.util.*
import com.makeevrserg.empireprojekt.util.files.Files
import org.bukkit.NamespacedKey
import org.bukkit.inventory.FurnaceRecipe
import org.bukkit.inventory.Recipe
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import java.awt.Shape

class EmpirePlugin : JavaPlugin() {

    companion object {
        lateinit var plugin: EmpirePlugin
            private set
    }


    lateinit var empireFiles: Files
    lateinit var empireItems: EmpireItems
    lateinit var translations: Translations
    lateinit var config: EmpireConfig
    lateinit var empireFontImages: EmpireFontImages
    private lateinit var commandManager: CommandManager
    lateinit var genericListener: GenericListener
    lateinit var categoryItems: MutableMap<String, CategoryItems.CategorySection>
    lateinit var getEveryDrop: MutableMap<String, MutableList<ItemDropListener.ItemDrop>>
    lateinit var empireSounds: EmpireSounds

    lateinit var empireConstants: EmpireConstats


    fun initPlugin() {
        empireConstants = EmpireConstats()
        translations = Translations()
        empireFiles = Files()
        empireSounds = EmpireSounds()
        empireFontImages =
            EmpireFontImages(empireFiles.fontImagesFile.getConfig())
        empireItems = EmpireItems()
        genericListener = GenericListener()
        commandManager = CommandManager()
        categoryItems =
            CategoryItems(
                empireFiles.guiFile.getConfig()?.getConfigurationSection("categories")
            ).categoriesMap
        getEveryDrop = genericListener._itemDropListener.everyDropByItem
        config =
            EmpireConfig(empireFiles.configFile.getConfig())

        empireSounds.getSounds()

    }

    override fun onEnable() {
        plugin = this
        initPlugin()
    }

    fun disablePlugin() {

        fun isCustomRecipe(key: NamespacedKey): Boolean {
            return key.key.contains(plugin.empireConstants.CUSTOM_RECIPE_KEY)
        }

        fun isCustomRecipe(recipe: FurnaceRecipe): Boolean {
            return isCustomRecipe(recipe.key)
        }

        fun isCustomRecipe(recipe: ShapedRecipe): Boolean {
            return isCustomRecipe(recipe.key)
        }

        fun isCustomRecipe(recipe: Recipe): Boolean {
            if (recipe is FurnaceRecipe)
                return isCustomRecipe(recipe as FurnaceRecipe)
            else if (recipe is ShapedRecipe)
                return isCustomRecipe(recipe as ShapedRecipe)
            return false

        }

        genericListener.onDisable()
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
            if (genericListener._craftEvent.empireRecipies.contains(id)) ite.remove()
        }


    }

    override fun onDisable() {
        disablePlugin()
        for (p in server.onlinePlayers)
            p.closeInventory()

    }
}