package com.astrainteractive.empire_items.models.yml_item

import kotlinx.serialization.Serializable

@Suppress("PROVIDED_RUNTIME_TOO_LOW")
@Serializable
data class Block(
    val breakParticle: String = "",
    val breakSound: String = "",
    val placeSound: String = "",
    val data: Int,
    val hardness: Int? = null,
    val ignoreCheck: Boolean = false,
    val generate: Generate? = null,
) {
    @Suppress("PROVIDED_RUNTIME_TOO_LOW")
    @Serializable
    data class Generate(
        val generateInChunkChance: Int,
        val minPerChunk: Int,
        val maxPerChunk: Int,
        val minPerDeposit: Int,
        val maxPerDeposit: Int,
        val minY: Int,
        val maxY: Int,
        val replaceBlocks: Map<String, Int> = mapOf(),
        val world: String? = null,
    )
}