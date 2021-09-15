package com.makeevrserg.empireprojekt.empire_items.events.villagers

import com.makeevrserg.empireprojekt.empire_items.events.villagers.data.VillagerItem
import com.makeevrserg.empireprojekt.empire_items.events.villagers.data.VillagerTrade

/**
 * Менеджер для кастомных трейдов у жителей
 */
class VillagerManager {


    companion object {
        /**
         * Список всех трейдов
         */
        lateinit var trades: List<VillagerTrade>

        /**
         * Список трейдов по профессиям
         */
        var villagerTradeByProfession: Map<String, List<VillagerItem>> = mutableMapOf()

        /**
         * Получения списка профессий, которые продают предмет
         */
        fun professionsByItem(item: String): List<String> {
            return mutableListOf<String>().apply {
                trades.forEach { trade->
                    addAll(trade.professionsByItemId(item))

                }
            }
//            for (trade in trades)
//                list.addAll(trade.professionsByItemId(item) ?: continue)
//            return if (list.isEmpty())
//                null
//            else
//                list
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