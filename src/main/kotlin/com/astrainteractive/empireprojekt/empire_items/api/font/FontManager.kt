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
    fun fontById() = map.toMap()
}