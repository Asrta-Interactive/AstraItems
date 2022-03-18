package com.astrainteractive.empire_items.api

import com.astrainteractive.astralibs.Logger
import com.astrainteractive.astralibs.async.AsyncHelper
import com.astrainteractive.empire_items.api.crafting.CraftingApi
import com.astrainteractive.empire_items.api.drop.DropApi
import com.astrainteractive.empire_items.api.font.FontApi
import com.astrainteractive.empire_items.api.items.data.ItemApi
import com.astrainteractive.empire_items.api.mobs.MobApi
import com.astrainteractive.empire_items.api.upgrade.UpgradeApi
import com.astrainteractive.empire_items.api.v_trades.VillagerTradeApi
import com.astrainteractive.empire_items.api.utils.Disableable
import com.astrainteractive.empire_items.empire_items.util.Timer
import kotlinx.coroutines.launch

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

    override suspend fun onEnable() {
        apiList.forEach {
            AsyncHelper.launch {
                it.onEnable()
            }
        }
    }

    override suspend fun onDisable() {
        apiList.forEach {
            it.onDisable()
        }
    }
}