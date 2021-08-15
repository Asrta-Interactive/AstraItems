package com.makeevrserg.empireprojekt.events.genericevents.drop.data


import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.events.mobs.data.EmpireMob
import empirelibs.EmpireYamlParser
import org.bukkit.configuration.ConfigurationSection


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
            return EmpireYamlParser.parseYamlConfig<List<ItemDrop>>(
                EmpirePlugin.empireFiles.dropsFile.getConfig(),
                object : TypeToken<List<ItemDrop?>?>() {}.type,
                listOf("loot", path)
            )!!
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