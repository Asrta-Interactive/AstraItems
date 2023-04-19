package com.atrainteractive.empire_items.models

import kotlinx.serialization.Serializable

@Suppress("PROVIDED_RUNTIME_TOO_LOW")
@Serializable
data class YmlSound(
    val id: String,
    val sounds: List<String>,
    val namespace: String = "empire_items"
)