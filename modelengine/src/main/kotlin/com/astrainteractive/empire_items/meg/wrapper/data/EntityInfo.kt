package com.astrainteractive.empire_items.meg.wrapper.data

import java.util.*

data class EntityInfo(
    val isSpawning: Boolean = true,
    var isAttacking: Boolean = false,
    val modelID: String,
    val empireID: String,
    val uuid: UUID
)
