package com.astrainteractive.empireprojekt.empire_items.api.drop

object DropManager {

    private var dropsMap:MutableMap<String,List<AstraDrop>> = mutableMapOf()

    fun clear(){
        dropsMap.clear()
    }
    fun loadDrops(){
        dropsMap = AstraDrop.getDrops()?.toMutableMap()?: mutableMapOf()
    }
    fun getDrops() = dropsMap.toMap()
}