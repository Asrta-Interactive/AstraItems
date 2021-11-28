package com.astrainteractive.empireprojekt.empire_items.api.font

object FontManager {


    var fonts = mutableListOf<AstraFont>()
    var map = mutableMapOf<String, AstraFont>()

    fun clear(){
        fonts.clear()
        map.clear()
    }
    fun load(){
        clear()
        fonts = AstraFont.getFonts().toMutableList()
        map = fonts.associateBy { it.id }.toMutableMap()
    }
    fun allFonts() = fonts.toList()
    fun playerFonts() = fonts.filter { !it.blockSend }
    fun fontById() = map.mapKeys { ":${it.key}:" }.mapValues { it.value.char }

    fun getOffsets() = mapOf(
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
}