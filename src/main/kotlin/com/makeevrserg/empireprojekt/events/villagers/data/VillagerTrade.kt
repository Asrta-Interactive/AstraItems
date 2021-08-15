package com.makeevrserg.empireprojekt.events.villagers.data

import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.makeevrserg.empireprojekt.EmpirePlugin
import empirelibs.EmpireYamlParser


data class VillagerTrade(
    @SerializedName("profession")
    val profession: String,
    @SerializedName("trades")
    val trades: List<VillagerItem>,
) {
    public fun professionToTrades(): Map<String, List<VillagerItem>> =
        mapOf(profession to trades)

    public fun isItemInProfession(id: String): List<String>? {
        val list = mutableListOf<String>()
        for (trade in trades)
            if (trade.resultItem.id == id)
                list.add(profession)
        return if (list.isEmpty())
            null
        else
            list
    }


    companion object {

        fun new(): List<VillagerTrade> {

            val trades = EmpireYamlParser.parseYamlConfig<List<VillagerTrade>>(
                EmpirePlugin.empireFiles.villagerTrades.getConfig(),
                object : TypeToken<List<VillagerTrade?>?>() {}.type,
                listOf("villager_trades")
            )!!
            return trades
        }
    }
}
