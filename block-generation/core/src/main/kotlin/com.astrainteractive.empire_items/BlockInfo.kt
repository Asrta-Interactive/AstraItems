package com.astrainteractive.empire_items

import org.bukkit.Material
import org.bukkit.block.Block

class BlockInfo(
    val block: Block,
    val material: Material,
    val data: Int,
    val facing: Map<String, Boolean>
)