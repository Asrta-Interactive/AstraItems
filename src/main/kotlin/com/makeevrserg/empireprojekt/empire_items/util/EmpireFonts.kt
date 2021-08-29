package com.makeevrserg.empireprojekt.empire_items.util

import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.empirelibs.EmpireYamlParser


data class EmpireFont(
    @SerializedName("id")
    val id: String,
    @SerializedName("path")
    val path: String,
    @SerializedName("height")
    val height: Int,
    @SerializedName("ascent")
    val ascent: Int,
    @SerializedName("chars")
    val chars: String,
    @SerializedName("send_blocked")
    val sendBlocked: Boolean = false
)

data class EmpireFonts(
    val _fontInfoValueById: Map<String, EmpireFont>,
    val _fontValueById: Map<String, String>,
    val playerFonts: Map<String, String>
) {


    companion object {


        fun new(): EmpireFonts {


            val fonts = EmpireYamlParser.fromYAML<List<EmpireFont>>(
                EmpirePlugin.empireFiles.fontImagesFile.getConfig(),
                object : TypeToken<List<EmpireFont?>?>() {}.type,
                listOf("font_images")
            )!!


            val fontsInfoMap = mutableMapOf<String, EmpireFont>()
            val fontsById = mutableMapOf<String, String>()
            val playerFonts = mutableMapOf<String, String>()
            for (font in fonts) {

                fontsInfoMap[":${font.id}:"] = font

                fontsById[":${font.id}:"] = font.chars

                if (!font.sendBlocked)
                    playerFonts[":${font.id}:"] = font.chars

            }
            val offsets = mapOf(
                "l_1" to "\uF801",
                "l_2" to "\uF802",
                "l_3" to "\uF803",
                "l_4" to "\uF804",
                "l_5" to "\uF805",
                "l_6" to "\uF806",
                "l_7" to "\uF807",
                "l_8" to "\uF808",
                "l_16" to "\uF809",
                "l_32" to "\uF80A",
                "l_64" to "\uF80B",
                "l_128" to "\uF80C",
                "l_512" to "\uF80D",
                "l_1024" to "\uF80E",
                "r_1" to "\uF821",
                "r_2" to "\uF822",
                "r_3" to "\uF823",
                "r_4" to "\uF824",
                "r_5" to "\uF825",
                "r_6" to "\uF826",
                "r_7" to "\uF827",
                "r_8" to "\uF828",
                "r_16" to "\uF829",
                "r_32" to "\uF82A",
                "r_64" to "\uF82B",
                "r_128" to "\uF82C",
                "r_512" to "\uF82D",
                "r_1024" to "\uF82E"
            )
            for ((offset, value) in offsets)
                fontsById[":$offset:"] = value

            return EmpireFonts(fontsInfoMap, fontsById, playerFonts)
        }
    }
}