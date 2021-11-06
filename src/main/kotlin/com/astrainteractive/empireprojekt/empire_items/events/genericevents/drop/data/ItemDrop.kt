package com.astrainteractive.empireprojekt.empire_items.events.genericevents.drop.data


import com.astrainteractive.astralibs.AstraYamlParser
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.astrainteractive.empireprojekt.EmpirePlugin


data class ItemDrop(
    @SerializedName("drop_from")
    val dropFrom: String = "",
    @SerializedName("id")
    val id: String = "",
    @SerializedName("min_amount")
    val minAmount: Int = 0,
    @SerializedName("max_amount")
    val maxAmount: Int = 0,
    @SerializedName("chance")
    val chance: Double = 0.0
) {
    companion object {

        private fun getList(path: String): List<ItemDrop>? {
            return AstraYamlParser.fromYAML<List<ItemDrop>>(
                EmpirePlugin.empireFiles.dropsFile.getConfig(),
                object : TypeToken<List<ItemDrop?>?>() {}.type,
                listOf("loot", path)
            )?: mutableListOf()
        }

        fun newBlocksDrops() = getList("blocks")

        fun newMobDrops() = getList("mobs")
        fun dropByKey(list:List<ItemDrop>?): Map<String, List<ItemDrop>> {
            val map = mutableMapOf<String,MutableList<ItemDrop>>()
            list?:return map
            for (drop in list){
                if (!map.containsKey(drop.dropFrom))
                    map[drop.dropFrom] = mutableListOf(drop)
                else
                    map[drop.dropFrom]!!.add(drop)
            }
            return map
        }
    }
}