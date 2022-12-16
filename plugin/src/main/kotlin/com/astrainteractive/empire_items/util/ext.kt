package com.astrainteractive.empire_items.util

import com.astrainteractive.empire_items.di.empireItemsApiModule
import com.astrainteractive.empire_items.util.EmpireItemsAPIExt.toAstraItem
import com.astrainteractive.empire_items.util.EmpireItemsAPIExt.toAstraItemOrItem
import com.astrainteractive.empire_itemss.api.models_ext.toItemStack
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.di.getValue

object EmpireItemsAPIExt {
    private val empireItemsApi by empireItemsApiModule


    fun toAstraItemOrItemByID(id: String?, amount: Int = 1): ItemStack? = id?.let { id ->
        return@let Material.getMaterial(id)?.let { ItemStack(it, amount) } ?: empireItemsApi.itemYamlFilesByID[id]?.toItemStack(amount)
    }

    fun String?.toAstraItemOrItem(amount: Int = 1): ItemStack? = toAstraItemOrItemByID(this, amount)
    fun String?.toAstraItem(amount: Int = 1): ItemStack? = empireItemsApi.itemYamlFilesByID[this]?.toItemStack()
}
