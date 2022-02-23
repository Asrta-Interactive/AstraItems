package com.astrainteractive.empire_items.api.v_trades

import com.astrainteractive.empire_items.api.utils.Disableable

object VillagerTradeApi : Disableable {

    var villagerTrades = mutableListOf<AstraVillagerTrade>()
    var mapTrades = mutableMapOf<String, AstraVillagerTrade>()

   override fun onDisable() {
        villagerTrades.clear()
        mapTrades.clear()

    }

    override fun onEnable() {
        villagerTrades = AstraVillagerTrade.getVillagerTrades().toMutableList()
        mapTrades = villagerTrades.associateBy { it.profession }.toMutableMap()
    }

    fun villagerTradeByProfession(name: String) = mapTrades[name]
}