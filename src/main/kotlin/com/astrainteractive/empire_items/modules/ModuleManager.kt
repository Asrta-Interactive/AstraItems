package com.astrainteractive.empire_items.modules

import com.astrainteractive.empire_items.api.utils.Disableable
import com.astrainteractive.empire_items.modules.enchants.EnchantManager

object ModuleManager: Disableable {

    lateinit var enchantManager:EnchantManager
    override fun onEnable() {
        enchantManager = EnchantManager()
    }

    override fun onDisable() {
        enchantManager.onDisable()
    }
}