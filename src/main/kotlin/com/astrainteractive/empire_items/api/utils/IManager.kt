package com.astrainteractive.empire_items.api.utils

interface IManager {
    suspend fun onEnable()
    suspend fun onDisable()
}