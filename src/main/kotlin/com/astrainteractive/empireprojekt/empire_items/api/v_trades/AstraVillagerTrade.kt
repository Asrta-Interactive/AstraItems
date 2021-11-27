package com.astrainteractive.empireprojekt.empire_items.api.v_trades

import com.astrainteractive.empireprojekt.empire_items.api.utils.getCustomItemsFiles
import org.bukkit.configuration.ConfigurationSection

data class AstraVillagerTrade(
    val profession:String,
    val trades:List<TradeItem>
){
    companion object{
        fun getVillagerTrades() = getCustomItemsFiles()?.mapNotNull {
            val fileConfig = it.getConfig()
            val section = fileConfig.getConfigurationSection("villagerTrades")
            section?.getKeys(false)?.mapNotNull {profession->
                getTVillagerTrade(section.getConfigurationSection(profession))
            }
        }?.flatten()?: listOf()

        fun getTVillagerTrade(s:ConfigurationSection?): AstraVillagerTrade? {
            s?:return null
            val profession = s.getString("profession")?:s.name
            val trades = TradeItem.getTrades(s.getConfigurationSection("trades"))
            return AstraVillagerTrade(
                profession = profession,
                trades = trades
            )
        }
    }
}
data class TradeItem(
    val chance:Int,
    val leftItem: SlotItem,
    val middleItem: SlotItem?,
    val resultItem: SlotItem,
    val minLevel:Int,
    val maxLevel:Int
){
    companion object{
        fun getTrades(section:ConfigurationSection?)=
            section?.getKeys(false)?.mapNotNull {
              val s = section.getConfigurationSection(it)?:return@mapNotNull null
                val chance = s.getInt("chance")
                val result = s.getString("id")?:s.name
                val amount = s.getInt("amount",1)
                val minLevel = s.getInt("minLevel",0)
                val maxLevel = s.getInt("maxLevel",5)+1
                val leftItem = SlotItem.getItem(s.getConfigurationSection("leftItem")) ?: return@mapNotNull null
                val middleItem = SlotItem.getItem(s.getConfigurationSection("middleItem"))
                val resultItem = SlotItem(result,amount)
                TradeItem(
                    chance = chance,
                    leftItem = leftItem,
                    middleItem = middleItem,
                    resultItem = resultItem,
                    minLevel = minLevel,
                    maxLevel = maxLevel
                )
            }?: listOf()

    }
}
data class SlotItem(
    val id:String,
    val amount:Int
){
    companion object{
        fun getItem(s:ConfigurationSection?): SlotItem? {
            s?:return null
            val amount = s.getInt("amount",1)
            val id = s.getString("id")?:return null
            return SlotItem(
                id = id,
                amount = amount
            )
        }
    }
}