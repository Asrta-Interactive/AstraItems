package com.astrainteractive.empire_items.empire_items.events.blocks

import org.bukkit.Location
import org.bukkit.Material

data class QueuedBlock(
    val id:String,
    val location: Location,
    val materialName: String,
    val faces: Map<String, Boolean>
) {
    val material: Material
        get() = Material.getMaterial(materialName)!!
}