package com.astrainteractive.empireprojekt.empire_items.api.upgrade

import com.astrainteractive.empireprojekt.empire_items.api.utils.getCustomItemsFiles
import org.bukkit.attribute.Attribute
import org.bukkit.configuration.ConfigurationSection

data class UpgradeLimit(
    val id:String,
    val map:Map<String,Double>
){

    fun byAttribute(attr:Attribute): Double? {
        return map[attr.name]
    }
    companion object{

        fun ConfigurationSection?.getDoubleMap(): Map<String, Double>? {
            return this?.getKeys(false)?.associate { key ->
                val k = key
                val v = this?.getDouble(key)
                Pair(k, v)
            }
        }

        fun getLimit(s:ConfigurationSection?): UpgradeLimit? {
            s?:return null
            val id = s.name
            val map =s.getDoubleMap()
            return UpgradeLimit(id=id,map = map?:return null)
        }
        fun getLimits()=
            getCustomItemsFiles()?.mapNotNull {
                val fileConfig = it.getConfig()
                val section = fileConfig.getConfigurationSection("upgradesLimit")
                section?.getKeys(false)?.mapNotNull {
                    UpgradeLimit.getLimit(section.getConfigurationSection(it))
                }
            }?.flatten()?.associateBy { it.id }?: mapOf()
    }
}
