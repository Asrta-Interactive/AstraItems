package com.atrainteractive.empire_items.models.recipies


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Suppress("PROVIDED_RUNTIME_TOO_LOW")
@Serializable
data class Furnace(
    val id: String,
    val result: String = id,
    val returns: String? = null,
    val amount: Int = 1,
    val input: String,
    @SerialName("cook_time")
    val cookTime: Int = 20,
    val exp: Int = 20,
)