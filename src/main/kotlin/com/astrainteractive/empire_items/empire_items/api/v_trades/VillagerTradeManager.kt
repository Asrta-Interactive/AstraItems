package com.astrainteractive.empire_items.empire_items.api.v_trades

object VillagerTradeManager {

    var villagerTrades = mutableListOf<AstraVillagerTrade>()
    var mapTrades = mutableMapOf<String, AstraVillagerTrade>()

    fun clear(){
        villagerTrades.clear()
        mapTrades.clear()

    }
    fun load(){
        clear()
        villagerTrades = AstraVillagerTrade.getVillagerTrades().toMutableList()
        mapTrades = villagerTrades.associateBy { it.profession }.toMutableMap()
    }

    fun villagerTradeByProfession(name:String) = mapTrades[name]
}