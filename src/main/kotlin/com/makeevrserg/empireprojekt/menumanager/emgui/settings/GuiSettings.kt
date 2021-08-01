package com.makeevrserg.empireprojekt.menumanager.emgui.settings

import com.makeevrserg.empireprojekt.EmpirePlugin
import empirelibs.EmpireUtils
import empirelibs.getHEXString

import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.inventory.ItemStack
import java.util.*

class GuiSettings {

    lateinit var workbenchUi: String
    lateinit var categoriesText: String
    lateinit var soundsText: String
    lateinit var dropButton: ItemStack
    lateinit var nextButton: ItemStack
    lateinit var prevButton: ItemStack
    lateinit var backButton: ItemStack
    lateinit var closeButton: ItemStack
    lateinit var giveButton: ItemStack
    lateinit var furnaceButton: ItemStack
    lateinit var craftingTableButton: ItemStack
    lateinit var workbenchSound: String
    lateinit var categoriesSound: String
    lateinit var categorySound: String

    init {
        val section = EmpirePlugin.empireFiles.guiFile.getConfig()?.getConfigurationSection("settings")!!
        workbenchUi = section.getHEXString("workbench_ui", "Крафт")
        categoriesText = section.getHEXString("categories_text", "Категории")
        soundsText = section.getHEXString("sounds_text", "Звуик")
        dropButton = EmpireUtils.getItemStackByName(section.getString("drop_btn", "PAPER")!!)
        nextButton = EmpireUtils.getItemStackByName(section.getString("next_btn", "PAPER")!!)
        prevButton = EmpireUtils.getItemStackByName(section.getString("prev_btn", "PAPER")!!)
        backButton = EmpireUtils.getItemStackByName(section.getString("back_btn", "PAPER")!!)
        closeButton = EmpireUtils.getItemStackByName(section.getString("close_btn", "PAPER")!!)
        giveButton = EmpireUtils.getItemStackByName(section.getString("give_btn", "PAPER")!!)
        furnaceButton = EmpireUtils.getItemStackByName(section.getString("furnace_btn", "PAPER")!!)
        craftingTableButton = EmpireUtils.getItemStackByName(section.getString("crafting_table_btn", "PAPER")!!)
        workbenchSound = section.getString("workbench_sound", Sound.ITEM_BOOK_PAGE_TURN.name.lowercase())!!
        categoriesSound = section.getString("categories_sound",Sound.ITEM_BOOK_PAGE_TURN.name.lowercase(Locale.getDefault()))!!
        categorySound = section.getString("category_sound", Sound.ITEM_BOOK_PAGE_TURN.name.lowercase())!!

    }
}