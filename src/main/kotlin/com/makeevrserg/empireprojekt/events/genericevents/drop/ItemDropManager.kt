package com.makeevrserg.empireprojekt.events.genericevents.drop

import com.makeevrserg.empireprojekt.EmpirePlugin
import org.bukkit.configuration.ConfigurationSection

class ItemDropManager {

    data class ItemDrop(
        val dropFrom: String,
        val item: String,
        val minAmount: Int,
        val maxAmount: Int,
        val chance: Double
    )

    var mobDrops: MutableMap<String, List<ItemDrop>>
    var itemDrops: MutableMap<String, List<ItemDrop>>
    var everyDropByItem: MutableMap<String, MutableList<ItemDrop>> = mutableMapOf()


    private fun initEveryDrop() {
        val map: MutableMap<String, List<ItemDrop>> = mutableMapOf()
        map.putAll(mobDrops)
        map.putAll(itemDrops)
        for (key in map.keys) {
            for (drop in map[key]!!) {
                if (everyDropByItem[drop.item] == null)
                    everyDropByItem[drop.item] = mutableListOf()
                everyDropByItem[drop.item]!!.add(drop)
            }
        }
    }

    private fun initDrop(section: ConfigurationSection?): MutableMap<String, List<ItemDrop>> {
        section ?: return mutableMapOf()
        val drop: MutableMap<String, List<ItemDrop>> = mutableMapOf()

        for (entityKey in section.getKeys(false)) {
            val list: MutableList<ItemDrop> = mutableListOf()
            for (item in section.getConfigurationSection(entityKey)!!.getKeys(false)) {
                val itemSect = section.getConfigurationSection(entityKey)!!.getConfigurationSection(item)!!
                list.add(
                    ItemDrop(
                        entityKey,
                        item,
                        itemSect.getInt("min_amount", 0),
                        itemSect.getInt("max_amount", 0),
                        itemSect.getDouble("chance", 0.0)
                    )
                )

            }
            drop[entityKey] = list
        }

        return drop

    }

    init {
        itemDrops = initDrop(EmpirePlugin.empireFiles.dropsFile.getConfig()?.getConfigurationSection("loot.blocks"))
        mobDrops = initDrop(EmpirePlugin.empireFiles.dropsFile.getConfig()?.getConfigurationSection("loot.mobs"))
        initEveryDrop()
    }
}