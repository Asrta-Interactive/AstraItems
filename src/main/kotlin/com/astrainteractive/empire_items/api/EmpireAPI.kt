package com.astrainteractive.empire_items.api

import com.astrainteractive.astralibs.Logger
import com.astrainteractive.astralibs.async.AsyncHelper
import com.astrainteractive.astralibs.valueOfOrNull
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
import org.bukkit.Material
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType

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

    fun isEmpireItem(item: String?) = ItemApi.getItemInfo(item) != null
    fun isMinecraftItem(item: String?) = Material.getMaterial(item ?: "") != null
    fun isMinecraftEntity(item: String?) = valueOfOrNull<EntityType>(item ?: "") != null
    fun isEmpireEntity(item: String?) = MobApi.getEmpireMob(item ?: "") != null
    fun isGameObjectOrItem(item: String?) =
        isEmpireItem(item) || isMinecraftItem(item) || isEmpireEntity(item) || isMinecraftEntity(item)

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