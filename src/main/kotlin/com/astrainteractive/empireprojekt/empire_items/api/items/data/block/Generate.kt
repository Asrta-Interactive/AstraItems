package com.astrainteractive.empireprojekt.empire_items.api.items.data.block

import com.astrainteractive.empireprojekt.empire_items.api.items.data.AstraItem.Companion.getIntOrNull
import org.bukkit.configuration.ConfigurationSection

data class Generate(
    val generateInChunkChance:Int,
    val minPerChunk:Int,
    val maxPerChunk:Int,
    val minPerDeposit:Int,
    val maxPerDeposit:Int,
    val minY:Int?,
    val maxY:Int?,
    val replaceBlocks:Map<String,Int>?,
    val world:String?
){
    companion object{
        fun getGenerate(s:ConfigurationSection?): Generate? {
            val generateInChunkChance = s?.getIntOrNull("generateInChunkChance")?:return null
            val minPerChunk = s.getInt("minPerChunk",0)
            val maxPerChunk = s.getIntOrNull("maxPerChunk")?:return null
            val minPerDeposit = s.getInt("minPerDeposit",0)
            val maxPerDeposit = s.getIntOrNull("maxPerDeposit")?:return null
            val minY = s.getInt("minY",0)
            val maxY = s.getIntOrNull("maxY")?:return null
            val world = s.getString("world")
            val replaceBlocks = s.getConfigurationSection("replaceBlocks")?.getKeys(false)?.associate { Pair(it,s.getInt("replaceBlocks.$it")) }

            return Generate(
                generateInChunkChance = generateInChunkChance,
                minPerChunk = minPerChunk,
                maxPerChunk = maxPerChunk,
                minPerDeposit = minPerDeposit,
                maxPerDeposit = maxPerDeposit,
                minY = minY,
                maxY = maxY,
                replaceBlocks = replaceBlocks,
                world = world
            )
        }
    }
}
