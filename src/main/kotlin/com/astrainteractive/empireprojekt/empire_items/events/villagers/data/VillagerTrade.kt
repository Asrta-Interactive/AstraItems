package com.astrainteractive.empireprojekt.empire_items.events.villagers.data

import com.astrainteractive.astralibs.AstraYamlParser
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.astrainteractive.empireprojekt.EmpirePlugin


data class VillagerTrade(
    @SerializedName("profession")
    val profession: String,
    @SerializedName("trades")
    val trades: List<VillagerItem>,
) {
    public fun professionToTrades(): Map<String, List<VillagerItem>> =
        mapOf(profession to trades)

    public fun professionsByItemId(id: String): List<String> {
        return trades.filter { it.resultItem.id == id }.map { profession }
//        for (trade in trades)
//            if (trade.resultItem.id == id)
//                list.add(profession)
//        return if (list.isEmpty())
//            null
//        else
//            list
    }


    companion object {

        fun new(): List<VillagerTrade> {

            val trades = AstraYamlParser.fromYAML<List<VillagerTrade>>(
                EmpirePlugin.empireFiles.villagerTrades.getConfig(),
                object : TypeToken<List<VillagerTrade?>?>() {}.type,
                listOf("villager_trades")
            )?: mutableListOf()
            return trades
        }
    }
}