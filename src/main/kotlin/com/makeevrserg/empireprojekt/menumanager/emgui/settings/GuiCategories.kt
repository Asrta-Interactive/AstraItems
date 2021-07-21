package com.makeevrserg.empireprojekt.menumanager.emgui.settings

import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.util.EmpireUtils
import com.makeevrserg.empireprojekt.util.getHEXString
import com.makeevrserg.empireprojekt.util.getHEXStringList
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.inventory.ItemStack


class GuiCategories {
    data class GuiCategory(
        val title: String,
        val name: String,
        val icon: ItemStack,
        val lore: List<String>,
        val items: List<String>
    ) {
        constructor(sect: ConfigurationSection) : this(
            sect.getHEXString("title", "title"),
            sect.getHEXString("name", "name"),
            EmpireUtils.getItemStackByName(sect.getString("icon", "PAPER")!!),
            sect.getHEXStringList("lore"),
            sect.getHEXStringList("items")
        )
    }

    lateinit var categoriesMap: Map<String, GuiCategory>

    init {
        val section = EmpirePlugin.empireFiles.guiFile.getConfig()?.getConfigurationSection("categories")!!
        val map = mutableMapOf<String, GuiCategory>()
        for (key in section.getKeys(false))
            map[key] = GuiCategory(section.getConfigurationSection(key)!!)
        categoriesMap = map
    }
}