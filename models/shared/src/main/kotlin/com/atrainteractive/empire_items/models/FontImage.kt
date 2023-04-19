package com.atrainteractive.empire_items.models

import kotlinx.serialization.Serializable

@Suppress("PROVIDED_RUNTIME_TOO_LOW")
@Serializable
data class FontImage(
    val id: String,
    val path: String,
    val height: Int = 12,
    val ascent: Int = 12,
    val data: Int,
    val blockSend: Boolean = false
) {
    val char: String
        get() = (count + data).toChar().toString()
    companion object {
        private const val count: Int = 0x3400
    }
}