package com.makeevrserg.empireprojekt.emgui.settings

import com.makeevrserg.empireprojekt.EmpirePlugin
import empirelibs.EmpireUtils
import empirelibs.getHEXString
import empirelibs.getHEXStringList

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