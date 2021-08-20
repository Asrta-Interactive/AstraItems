package com.makeevrserg.empireprojekt.empire_items.events.villagers

import com.makeevrserg.empireprojekt.empire_items.events.villagers.data.VillagerItem
import com.makeevrserg.empireprojekt.empire_items.events.villagers.data.VillagerTrade

class VillagerManager {


    companion object {
        lateinit var trades: List<VillagerTrade>
        var villagerTradeByProfession: Map<String, List<VillagerItem>> = mutableMapOf()

        fun professionsByItem(item: String): List<String>? {
            val list = mutableListOf<String>()
            for (trade in trades)
                list.addAll(trade.isItemInProfession(item) ?: continue)
            return if (list.isEmpty())
                null
            else
                list
        }

    }


    init {

        trades = VillagerTrade.new()
        val map = mutableMapOf<String, List<VillagerItem>>()
        for (trade in trades)
            map.putAll(trade.professionToTrades())
        villagerTradeByProfession = map


    }
}