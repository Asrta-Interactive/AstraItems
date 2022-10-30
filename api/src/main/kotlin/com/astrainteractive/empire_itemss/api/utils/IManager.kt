package com.astrainteractive.empire_itemss.api.utils

interface IManager {
    suspend fun onEnable()
    suspend fun onDisable()
}