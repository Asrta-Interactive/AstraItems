package com.astrainteractive.empireprojekt.empire_items.events.upgrades.data

import com.astrainteractive.astralibs.AstraYamlParser
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.astrainteractive.empireprojekt.EmpirePlugin
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
        fun newList(): List<EmpireUpgrade> = AstraYamlParser.fromYAML<List<EmpireUpgrade>>(
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
