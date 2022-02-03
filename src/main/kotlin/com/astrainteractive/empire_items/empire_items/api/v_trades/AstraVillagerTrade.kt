package com.astrainteractive.empire_items.empire_items.api.v_trades

import com.astrainteractive.empire_items.empire_items.api.items.data.EmpireItem.Companion.getIntOrNull
import com.astrainteractive.empire_items.empire_items.api.utils.getCustomItemsFiles
import org.bukkit.configuration.ConfigurationSection
import kotlin.random.Random

data class AstraVillagerTrade(
    val profession: String,
    val trades: List<TradeItem>
) {
    companion object {
        fun getVillagerTrades() = getCustomItemsFiles()?.mapNotNull file@{
            val fileConfig = it.getConfig()
            val section = fileConfig.getConfigurationSection("villagerTrades")
            section?.getKeys(false)?.mapNotNull { profession ->
                val s = section.getConfigurationSection(profession) ?: return@mapNotNull null
                AstraVillagerTrade(
                    profession = s.getString("profession") ?: s.name,
                    trades = TradeItem.getTrades(s.getConfigurationSection("trades"))
                )
            }
        }?.flatten() ?: listOf()
    }
}

data class TradeItem(
    val id: String,
    val chance: Int,
    val amount: Int,
    val leftItem: SlotItem,
    val middleItem: SlotItem?,
    val minLevel: Int,
    val maxLevel: Int,
    val minUses: Int,
    val maxUses: Int
) {
    companion object {
        fun getTrades(section: ConfigurationSection?) =
            section?.getKeys(false)?.mapNotNull {
                val s = section.getConfigurationSection(it) ?: return@mapNotNull null
                TradeItem(
                    id = s.getString("id") ?: s.name,
                    chance = s.getInt("chance", 100),
                    amount = s.getInt("amount", 1),
                    leftItem = SlotItem.getItem(s.getConfigurationSection("leftItem"))!!,
                    middleItem = SlotItem.getItem(s.getConfigurationSection("middleItem")),
                    minLevel = s.getInt("minLevel", 1),
                    maxLevel = s.getInt("maxLevel", 1) + 1,
                    minUses = s.getInt("minUses", 1),
                    maxUses = s.getInt("maxUses", 1) + 1,
                )
            } ?: listOf()

    }
}

data class SlotItem(
    val id: String,
    private val _amount: Int,
    private val minAmount: Int?,
    private val maxAmount: Int?
) {
    val amount: Int
        get() {
            return if (minAmount != null && maxAmount != null)
                Random(System.currentTimeMillis()).nextInt(minAmount, maxAmount)
            else _amount
        }

    companion object {
        fun getItem(s: ConfigurationSection?): SlotItem? {
            s ?: return null
            return SlotItem(
                id = s.getString("id") ?: return null,
                _amount = s.getInt("amount", 1),
                minAmount = s.getIntOrNull("minAmount"),
                maxAmount = s.getIntOrNull("maxAmount")
            )
        }
    }
}