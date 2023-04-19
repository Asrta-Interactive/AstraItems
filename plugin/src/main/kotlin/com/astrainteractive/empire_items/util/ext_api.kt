package com.astrainteractive.empire_items.util

import com.astrainteractive.empire_items.di.empireItemsApiModule
import com.astrainteractive.empire_items.api.models_ext.toItemStack
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.di.getValue

object ext_api {
    private val empireItemsApi by empireItemsApiModule
    fun toAstraItemOrItemByID(id: String?, amount: Int = 1): ItemStack? = id?.let { id ->
        return@let Material.getMaterial(id)?.let { ItemStack(it, amount) } ?: empireItemsApi.itemYamlFilesByID[id]?.toItemStack(amount)
    }
    fun String?.toAstraItemOrItem(amount: Int = 1): ItemStack? = toAstraItemOrItemByID(this, amount)
    fun String?.toAstraItem(amount: Int = 1): ItemStack? = empireItemsApi.itemYamlFilesByID[this]?.toItemStack()
}
