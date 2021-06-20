package com.makeevrserg.empireprojekt.util

import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.FileConfiguration

class EmpireFontImages(fontFileConfig: FileConfiguration?) {

    private val _fontValueById: MutableMap<String, String> = mutableMapOf()
    private val _fontsInfo:MutableMap<String,EmpireFont> = mutableMapOf()
    val fontValueById: MutableMap<String, String>
        get() = _fontValueById
    val fontsInfo: MutableMap<String,EmpireFont>
        get() = _fontsInfo

    private fun initFonts(fontFileConfig: FileConfiguration?) {
        fontFileConfig ?: return
        val fontImagesConfig: ConfigurationSection = fontFileConfig.getConfigurationSection("font_images") ?: return

        for (fontID in fontImagesConfig.getKeys(false)) {
            val fontValue: String =
                fontImagesConfig.getConfigurationSection(fontID)!!
                    .getString("chars") ?: continue

            //fontValue = (Integer.parseInt(fontValue.substring(2), 16).toChar()).toString()


            _fontValueById[":$fontID:"] = fontValue

            val currentFontConfig = fontImagesConfig.getConfigurationSection(fontID)!!
            _fontsInfo[":$fontID:"] = EmpireFont(
                currentFontConfig.getString("namespace","empire_items")?:"empire_items",
                currentFontConfig.getBoolean("send_blocked",false),
                currentFontConfig.getString("path")?:continue,
                fontValue,
                currentFontConfig.getInt("offset",8),
                currentFontConfig.getInt("size",10)
            )
        }
        for (offsedId in fontFileConfig.getConfigurationSection("offsets")?.getKeys(false) ?: return)
            _fontValueById[":$offsedId:"] = fontFileConfig.getConfigurationSection("offsets")!!.getString(offsedId)!!

    }

    data class EmpireFont(
        val namespace: String,
        val sendBlocked: Boolean,
        val path: String,
        val chars: String,
        val size: Int,
        val offset: Int
    )

    init {
        initFonts(fontFileConfig)
    }

}