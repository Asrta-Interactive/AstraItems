package com.atrainteractive.empire_items.models

import kotlinx.serialization.Serializable
import kotlin.random.Random


@Suppress("PROVIDED_RUNTIME_TOO_LOW")
@Serializable
data class Loot(
    val id: String,
    val dropFrom: String,
    val minAmount: Int = 1,
    val maxAmount: Int = 2,
    val chance: Double = 0.2
)