package com.astrainteractive.empireprojekt.items.data.block

import com.google.gson.annotations.SerializedName

data class Generate(
    @SerializedName("min_per_deposite")
    val minPerDeposite:Int,
    @SerializedName("max_per_deposite")
    val maxPerDeposite:Int,
    @SerializedName("chunk")
    val generateInChunkChance:Int,
    @SerializedName("max_per_chunk")
    val maxPerChunk:Int,
    @SerializedName("min_y")
    val minY:Int?,
    @SerializedName("max_y")
    val maxY:Int?,
    @SerializedName("replace_blocks")
    val replaceBlocks:Map<String,Double>,
    @SerializedName("world")
    val world:String?
)
