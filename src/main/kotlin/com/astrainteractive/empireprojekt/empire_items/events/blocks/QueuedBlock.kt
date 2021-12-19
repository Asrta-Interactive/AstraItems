package com.astrainteractive.empireprojekt.empire_items.events.blocks

import org.bukkit.Location
import org.bukkit.Material

data class QueuedBlock(
    val l: Location,
    val m: String,
    val f: Map<String, Boolean>
) {
    val mat: Material
        get() = Material.getMaterial(m)!!
}