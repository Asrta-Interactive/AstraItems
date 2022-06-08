package com.astrainteractive.empire_items.modules

import com.astrainteractive.empire_items.api.utils.Disableable
import com.astrainteractive.empire_items.modules.action_inventories.ActionInventories
import com.astrainteractive.empire_items.modules.enchants.EnchantManager

object ModuleManager: Disableable {

    lateinit var enchantManager:EnchantManager
    override suspend fun onEnable() {
        enchantManager = EnchantManager()
        ActionInventories.onEnable()
    }

    override suspend fun onDisable() {
        enchantManager.onDisable()
        ActionInventories.onDisable()
    }
}