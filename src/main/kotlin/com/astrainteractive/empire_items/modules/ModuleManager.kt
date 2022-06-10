package com.astrainteractive.empire_items.modules

import com.astrainteractive.empire_items.api.utils.IManager
import com.astrainteractive.empire_items.modules.enchants.EnchantManager

object ModuleManager: IManager {

    lateinit var enchantManager:EnchantManager
    override suspend fun onEnable() {
        enchantManager = EnchantManager()
    }

    override suspend fun onDisable() {
        enchantManager.onDisable()
    }
}