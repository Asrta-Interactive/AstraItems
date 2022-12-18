package com.astrainteractive.empire_items

import org.bukkit.Material
import org.bukkit.block.Block

interface IFastBlockPlacer {
    suspend fun setTypeFast(
        type: Material,
        facing: Map<String, Boolean> = emptyMap(),
        blockData: Int? = null,
        vararg block: Block
    )
}