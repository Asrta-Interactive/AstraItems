package com.astrainteractive.empire_items.modules.enchants.api

import com.astrainteractive.empire_items.api.utils.BukkitConstant
import org.bukkit.persistence.PersistentDataType
import org.jetbrains.kotlin.gradle.utils.`is`

object EmpireEnchants {
    val EMPIRE_ENCHANT
        get() = BukkitConstant("EMPIRE_ENCHANT", PersistentDataType.BYTE)
    val LAVA_WALKER_ENCHANT
        get() = BukkitConstant("LAVA_WALKER_ENCHANT", PersistentDataType.INTEGER)
    val VAMPIRISM
        get() = BukkitConstant("VAMPIRISM", PersistentDataType.INTEGER)
    val ANTI_FALL
        get() = BukkitConstant("ANTI_FALL", PersistentDataType.INTEGER)
    val DOUBLE_JUMP
        get() = BukkitConstant("DOUBLE_JUMP", PersistentDataType.INTEGER)
    val INFINITE_POTION_EFFECT
        get() = BukkitConstant("INFINITE_POTION_EFFECT", PersistentDataType.INTEGER)
    val FROST_ASPECT
        get() = BukkitConstant("FROST_ASPECT", PersistentDataType.INTEGER)
    val BUTCHER
        get() = BukkitConstant("BUTCHER", PersistentDataType.INTEGER)
    val VYDERLIGHT
        get() = BukkitConstant("VYDERLIGHT", PersistentDataType.INTEGER)
    val AQUALIGHT
        get() = BukkitConstant("AQUALIGHT", PersistentDataType.INTEGER)
    val STRIKE
        get() = BukkitConstant("STRIKE", PersistentDataType.INTEGER)

    val values: List<BukkitConstant<Int, Int>>
        get() = listOf(
            LAVA_WALKER_ENCHANT,
            VAMPIRISM,
            STRIKE,
            FROST_ASPECT,
            BUTCHER,
            VYDERLIGHT,
            AQUALIGHT,
            ANTI_FALL,
            INFINITE_POTION_EFFECT,
            DOUBLE_JUMP
        )
    val byKey: Map<String, BukkitConstant<Int, Int>>
        get() = values.associateBy { it.value.key.uppercase() }
}