package com.astrainteractive.empireprojekt.empire_items.api.drop

import com.astrainteractive.empireprojekt.EmpirePlugin
import org.bukkit.configuration.ConfigurationSection

data class AstraDrop(
    val dropFrom:String,
    val id:String,
    val minAmount:Int,
    val maxAmount:Int,
    val percent:Double
){

    companion object{
        fun getDrops(): Map<String, List<AstraDrop>> {
                val fileConfig = EmpirePlugin.empireFiles.dropsFile.getConfig().getConfigurationSection("loot")
                //loot.<dropFrom>
                return getMapDrop(fileConfig)
            }

        private fun getMapDrop(s:ConfigurationSection?)= s?.getKeys(false)?.associate { dropFrom->
            val section = s.getConfigurationSection(dropFrom)
            //loot.<dropFrom>.<itemId>
            val drops = section?.getKeys(false)?.mapNotNull {itemId->
                getSingleDrop(dropFrom,itemId,section.getConfigurationSection(itemId))
            }?: listOf()
            Pair(dropFrom,drops)
        }?: mapOf()

        private fun getSingleDrop(_dropFrom:String, _itemId:String, s:ConfigurationSection?): AstraDrop? {
            s?:return null
            val dromFrom = _dropFrom
            val id = _itemId
            val minAmount = s.getInt("minAmount",0)
            val maxAmount = s.getInt("maxAmount",0)
            val percent = s.getDouble("chance",0.0)
            return AstraDrop(
                dropFrom = dromFrom,
                id = id,
                minAmount = minAmount,
                maxAmount = maxAmount,
                percent = percent
            )
        }
    }
}
