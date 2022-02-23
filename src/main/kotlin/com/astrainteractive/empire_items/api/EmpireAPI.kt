package com.astrainteractive.empire_items.api

import com.astrainteractive.empire_items.api.crafting.CraftingApi
import com.astrainteractive.empire_items.api.drop.DropApi
import com.astrainteractive.empire_items.api.font.FontApi
import com.astrainteractive.empire_items.api.items.data.ItemApi
import com.astrainteractive.empire_items.api.mobs.MobApi
import com.astrainteractive.empire_items.api.upgrade.UpgradeApi
import com.astrainteractive.empire_items.api.v_trades.VillagerTradeApi
import com.astrainteractive.empire_items.api.utils.Disableable

object EmpireAPI : Disableable {
    private val apiList = mutableListOf<Disableable>(
        DropApi,
        FontApi,
        ItemApi,
        CraftingApi,
        MobApi,
        UpgradeApi,
        VillagerTradeApi
    )

    override fun onEnable() {
        apiList.forEach(Disableable::onEnable)
    }

    override fun onDisable() {
        apiList.forEach(Disableable::onDisable)
    }
}