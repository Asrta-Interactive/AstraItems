package com.makeevrserg.empireprojekt.empire_items.events.upgrades.data

import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.empirelibs.EmpireYamlParser
import org.bukkit.attribute.Attribute

data class EmpireUpgrade(
    @SerializedName("id")
    val id: String,
    @SerializedName("attribute")
    val attribute: Attribute,
    @SerializedName("add_min")
    val add_min: Double,
    @SerializedName("add_max")
    val add_max: Double
) {


    companion object {
        fun newList(): List<EmpireUpgrade> = EmpireYamlParser.fromYAML<List<EmpireUpgrade>>(
            EmpirePlugin.empireFiles.upgradesFile.getConfig(),
            object : TypeToken<List<EmpireUpgrade?>?>() {}.type,
            listOf("upgrades")
        )?: mutableListOf()

        fun newMap(): Map<String, List<EmpireUpgrade>> {

            val map = mutableMapOf<String, MutableList<EmpireUpgrade>>()
            val list = newList()
            for (upgrade in list)
                if (!map.containsKey(upgrade.id))
                    map[upgrade.id] = mutableListOf(upgrade)
                else
                    map[upgrade.id]!!.add(upgrade)


            return map
        }
    }
}
