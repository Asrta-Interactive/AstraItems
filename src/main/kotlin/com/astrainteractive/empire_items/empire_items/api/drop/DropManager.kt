package com.astrainteractive.empire_items.empire_items.api.drop

object DropManager {

    private var dropsMap:MutableList<AstraDrop> = mutableListOf()

    fun clear(){
        dropsMap.clear()
    }
    fun loadDrops(){
        dropsMap = AstraDrop.getDrops().toMutableList()
    }
    fun getDropsFrom(dropFrom:String) = dropsMap.filter { it.dropFrom==dropFrom }.toSet().toMutableList()
    fun getDropsById(id:String) = dropsMap.filter { it.id==id }.toSet().toMutableList()

}