package com.makeevrserg.empireprojekt.empire_items.util

import com.makeevrserg.empireprojekt.EmpirePlugin
import org.bukkit.NamespacedKey



enum class BetterConstants(public val value:NamespacedKey){
    EMPIRE_ID(NamespacedKey(EmpirePlugin.instance, "id")),
    GENERIC_ATTACK_DAMAGE(NamespacedKey(EmpirePlugin.instance, "GENERIC_ATTACK_DAMAGE")),
    GENERIC_ATTACK_KNOCKBACK(NamespacedKey(EmpirePlugin.instance, "GENERIC_ATTACK_KNOCKBACK")),
    GENERIC_ATTACK_SPEED(NamespacedKey(EmpirePlugin.instance, "GENERIC_ATTACK_SPEED")),
    GENERIC_MAX_HEALTH(NamespacedKey(EmpirePlugin.instance, "GENERIC_MAX_HEALTH")),
    GENERIC_KNOCKBACK_RESISTANCE(NamespacedKey(EmpirePlugin.instance, "GENERIC_KNOCKBACK_RESISTANCE")),
    GENERIC_ARMOR_TOUGHNESS(NamespacedKey(EmpirePlugin.instance, "GENERIC_ARMOR_TOUGHNESS")),
    GENERIC_ARMOR(NamespacedKey(EmpirePlugin.instance, "GENERIC_ARMOR")),
    ITEM_UPGRADE_COUNT(NamespacedKey(EmpirePlugin.instance, "ITEM_UPGRADE_COUNT")),
    FIXED_ITEM(NamespacedKey(EmpirePlugin.instance, "FIXED_ITEM")),
    MAX_CUSTOM_DURABILITY(NamespacedKey(EmpirePlugin.instance, "MAX_CUSTOM_DURABILITY")),
    EMPIRE_DURABILITY(NamespacedKey(EmpirePlugin.instance, "EMPIRE_DURABILITY")),
    EMPIRE_GUN_CURRENT_CLIP_SIZE(NamespacedKey(EmpirePlugin.instance, "EMPIRE_GUN_CURRENT_CLIP_SIZE")),
    GRENADE_EXPLOSION_POWER(NamespacedKey(EmpirePlugin.instance, "GRENADE_EXPLOSION_POWER")),
    MOLOTOV(NamespacedKey(EmpirePlugin.instance, "MOLOTOV")),
    GRAPPLING_HOOK(NamespacedKey(EmpirePlugin.instance, "GRAPPLING_HOOK")),
    SOUL_BIND(NamespacedKey(EmpirePlugin.instance, "SOUL_BIND")),
    HAMMER_ENCHANT(NamespacedKey(EmpirePlugin.instance, "ITEM_HAMMER")),
    LAVA_WALKER_ENCHANT(NamespacedKey(EmpirePlugin.instance, "LAVA_WALKER_ENCHANT")),
    VAMPIRISM_ENCHANT(NamespacedKey(EmpirePlugin.instance, "VAMPIRISM_ENCHANT")),
    CUSTOM_RECIPE_KEY(NamespacedKey(EmpirePlugin.instance, "CUSTOM_RECIPE_KEY"))
}

