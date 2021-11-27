package com.astrainteractive.empireprojekt.empire_items.api.utils

import org.bukkit.inventory.meta.ItemMeta


fun <T,Z> ItemMeta.setPersistentDataType(b: BukkitConstant<T, Z>, value:Z){
    persistentDataContainer.set(
        b.value,
        b.dataType,
        value?:return
    )
}

fun <T,Z> ItemMeta?.getPersistentData(b: BukkitConstant<T, Z>)=
    this?.persistentDataContainer?.get(
        b.value,
        b.dataType
    )
