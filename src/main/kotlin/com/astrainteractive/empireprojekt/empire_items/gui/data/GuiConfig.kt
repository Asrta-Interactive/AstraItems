package com.astrainteractive.empireprojekt.empire_items.gui.data

import com.astrainteractive.astralibs.getHEXString
import com.astrainteractive.astralibs.getHEXStringList
import com.astrainteractive.empireprojekt.EmpirePlugin
import com.astrainteractive.empireprojekt.empire_items.api.items.data.ItemManager.getItemStack
import com.astrainteractive.empireprojekt.empire_items.api.utils.setDisplayName
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.inventory.ItemStack

data class GuiConfig(
    val settings: Settings,
    val categories: Map<String, Category>?
) {
    companion object {
        fun getGuiConfig(): GuiConfig {
            val fileConfig = EmpirePlugin.empireFiles.guiConfig.getConfig()
            val settings = Settings.getSettings(fileConfig.getConfigurationSection("settings"))
            val categories = fileConfig.getConfigurationSection("categories")?.getKeys(false)?.mapNotNull {
                val section = fileConfig.getConfigurationSection("categories.$it")
                Category.getCategory(section)
            }?.associateBy { it.id }
            return GuiConfig(settings = settings,categories = categories)
        }
    }

    data class Settings(
        val workbenchText: String,
        val categoriesText: String,
        val soundsText: String,
        val moreButton: ItemStack,
        val nextButton: ItemStack,
        val prevButton: ItemStack,
        val backButton: ItemStack,
        val closeButton: ItemStack,
        val giveButton: ItemStack,
        val furnaceButton: ItemStack,
        val craftingTableButton: ItemStack,
        val workbenchSound: String?,
        val categoriesSound: String?,
        val categorySound: String?
    ) {
        companion object {
            private fun getItemStackButton(name: String) = ItemStack(Material.PAPER).apply { setDisplayName(name) }
            fun getSettings(s: ConfigurationSection?): Settings {
                val workbenchText = s?.getHEXString("workbenchText","")?:""
                val categoriesText = s?.getHEXString("categoriesText", "")?:""
                val soundsText = s?.getHEXString("soundsText", "")?:""
                val moreButton = s?.getString("moreButton").getItemStack() ?: getItemStackButton("Подробнее")
                val nextButton = s?.getString("nextButton").getItemStack() ?: getItemStackButton("Дальше")
                val prevButton = s?.getString("prevButton").getItemStack() ?: getItemStackButton("Раньше")
                val backButton = s?.getString("backButton").getItemStack() ?: getItemStackButton("Назад")
                val closeButton = s?.getString("closeButton").getItemStack() ?: getItemStackButton("Закрыть")
                val giveButton = s?.getString("giveButton").getItemStack() ?: getItemStackButton("Получить")
                val furnaceButton = s?.getString("furnaceButton").getItemStack() ?: getItemStackButton("Плавка")
                val craftingTableButton =
                    s?.getString("craftingTableButton").getItemStack() ?: getItemStackButton("Крафти")
                val workbenchSound = s?.getString("workbenchSound")
                val categoriesSound = s?.getString("categoriesSound")
                val categorySound = s?.getString("categorySound")
                return Settings(
                    workbenchText = workbenchText,
                    categoriesText = categoriesText,
                    soundsText = soundsText,
                    moreButton = moreButton,
                    nextButton = nextButton,
                    prevButton = prevButton,
                    backButton = backButton,
                    closeButton = closeButton,
                    giveButton = giveButton,
                    furnaceButton = furnaceButton,
                    craftingTableButton = craftingTableButton,
                    workbenchSound = workbenchSound,
                    categoriesSound = categoriesSound,
                    categorySound = categorySound
                )

            }

        }

    }

    data class Category(
        val id: String,
        val title: String,
        val name: String,
        val icon: ItemStack,
        val lore: List<String>,
        val permission: String?,
        val items: List<String>
    ) {

        fun toItemStack(): ItemStack {
            val itemMeta = icon.itemMeta!!
            itemMeta.setDisplayName(name)
            itemMeta.lore = lore
            icon.itemMeta = itemMeta
            return icon
        }

        companion object {
            fun getCategory(s: ConfigurationSection?): Category? {
                val id = s?.name ?: return null
                val title = s.getHEXString("title", "")
                val name = s.getHEXString("name", "")
                val icon = s.getString("icon")?.getItemStack() ?: ItemStack(Material.PAPER)
                val lore = s.getHEXStringList("lore")
                val permission = s.getString("permission")
                val items = s.getStringList("items")
                return Category(
                    id = id,
                    title = title,
                    name = name,
                    icon = icon,
                    lore = lore,
                    permission = permission,
                    items = items
                )
            }
        }
    }
}