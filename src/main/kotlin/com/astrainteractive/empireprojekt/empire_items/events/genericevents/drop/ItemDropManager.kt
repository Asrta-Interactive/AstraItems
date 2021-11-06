package com.astrainteractive.empireprojekt.empire_items.events.genericevents.drop

import com.astrainteractive.empireprojekt.EmpirePlugin
import com.astrainteractive.empireprojekt.empire_items.events.genericevents.drop.data.ItemDrop

class ItemDropManager {



    var mobDrops: Map<String, List<ItemDrop>>
    var itemDrops: Map<String, List<ItemDrop>>
    var everyDropByItem: MutableMap<String, MutableList<ItemDrop>> = mutableMapOf()


    private fun initEveryDrop() {
        val map: MutableMap<String, List<ItemDrop>> = mutableMapOf()
        map.putAll(mobDrops)
        map.putAll(itemDrops)
        for (key in map.keys) {
            for (drop in map[key]!!) {
                if (everyDropByItem[drop.id] == null)
                    everyDropByItem[drop.id] = mutableListOf()
                everyDropByItem[drop.id]!!.add(drop)
            }
        }
    }

    init {
        val config = EmpirePlugin.empireFiles.dropsFile.getConfig()
        itemDrops = ItemDrop.dropByKey(ItemDrop.newBlocksDrops())
        mobDrops = ItemDrop.dropByKey(ItemDrop.newMobDrops())
        initEveryDrop()
    }
}