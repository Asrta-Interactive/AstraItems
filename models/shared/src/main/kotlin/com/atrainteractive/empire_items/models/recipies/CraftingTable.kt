package com.atrainteractive.empire_items.models.recipies

import kotlinx.serialization.Serializable

@Suppress("PROVIDED_RUNTIME_TOO_LOW")
@Serializable
data class CraftingTable(
    val id: String,
    val result: String = id,
    val amount: Int = 1,
    val pattern: List<String>,
    val ingredients: Map<Char, String>,
)