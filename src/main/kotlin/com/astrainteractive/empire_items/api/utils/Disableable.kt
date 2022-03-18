package com.astrainteractive.empire_items.api.utils

interface Disableable {
    suspend fun onEnable()
    suspend fun onDisable()
}
