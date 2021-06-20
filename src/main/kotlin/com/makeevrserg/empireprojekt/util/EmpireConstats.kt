package com.makeevrserg.empireprojekt.util

import com.makeevrserg.empireprojekt.EmpirePlugin
import org.bukkit.NamespacedKey

class EmpireConstats {
    val plugin: EmpirePlugin = EmpirePlugin.plugin
    //public val EMPIRE_GUN:EmpireGun = EmpireGun(plugin)
    val empireID = NamespacedKey(plugin, "id")
    val EMPIRE_ATTACK_DAMAGE = NamespacedKey(plugin, "GENERIC_ATTACK_DAMAGE")
    val EMPIRE_ATTACK_KNOCKBACK = NamespacedKey(plugin, "GENERIC_ATTACK_KNOCKBACK")
    val EMPIRE_ATTACK_SPEED = NamespacedKey(plugin, "GENERIC_ATTACK_SPEED")
    val EMPIRE_MAX_HEALTH = NamespacedKey(plugin, "GENERIC_MAX_HEALTH")
    val EMPIRE_KNOCKBACK_RESISTANCE = NamespacedKey(plugin, "GENERIC_KNOCKBACK_RESISTANCE")
    val EMPIRE_ARMOR_TOUGHNESS = NamespacedKey(plugin, "GENERIC_ARMOR_TOUGHNESS")
    val EMPIRE_ARMOR = NamespacedKey(plugin, "GENERIC_ARMOR")
    val ITEM_UPGRADE_COUNT = NamespacedKey(plugin, "ITEM_UPGRADE_COUNT")

    var MAX_CUSTOM_DURABILITY: NamespacedKey = NamespacedKey(plugin, "MAX_CUSTOM_DURABILITY")
    var EMPIRE_DURABILITY: NamespacedKey = NamespacedKey(plugin, "EMPIRE_DURABILITY")

    var EMPIRE_GUN_CURRENT_CLIP_SIZE: NamespacedKey = NamespacedKey(plugin, "EMPIRE_GUN_CURRENT_CLIP_SIZE")

    val GRENADE_EXPLOSION_POWER = NamespacedKey(plugin, "GRENADE_EXPLOSION_POWER")
    val MOLOTOV = NamespacedKey(plugin, "MOLOTOV")
    val HAMMER_ENCHANT = NamespacedKey(plugin, "ITEM_HAMMER")
    val LAVA_WALKER_ENCHANT = NamespacedKey(plugin, "LAVA_WALKER_ENCHANT")
    val VAMPIRISM_ENCHANT = NamespacedKey(plugin, "VAMPIRISM_ENCHANT")
    val CUSTOM_RECIPE_KEY = "emp_"
    fun getEnchantsMap():Map<String,NamespacedKey>{
        return mapOf<String,NamespacedKey>(
            "GRENADE_EXPLOSION_POWER" to GRENADE_EXPLOSION_POWER,
            "MOLOTOV" to MOLOTOV,
            "HAMMER_ENCHANT" to HAMMER_ENCHANT,
            "LAVA_WALKER_ENCHANT" to LAVA_WALKER_ENCHANT,
            "VAMPIRISM_ENCHANT" to VAMPIRISM_ENCHANT

        )
    }

    fun getUpgradesMap(): Map<String, NamespacedKey> {
        return mapOf<String,NamespacedKey>(
            "GENERIC_ATTACK_DAMAGE" to EMPIRE_ATTACK_DAMAGE,
            "GENERIC_ATTACK_KNOCKBACK" to EMPIRE_ATTACK_KNOCKBACK,
            "GENERIC_ATTACK_SPEED" to EMPIRE_ATTACK_SPEED,
            "GENERIC_MAX_HEALTH" to EMPIRE_MAX_HEALTH,
            "GENERIC_KNOCKBACK_RESISTANCE" to EMPIRE_KNOCKBACK_RESISTANCE,
            "GENERIC_ARMOR_TOUGHNESS" to EMPIRE_ARMOR_TOUGHNESS,
            "GENERIC_ARMOR" to EMPIRE_ARMOR
        )
    }


//    class EmpireGun(plugin:EmpirePlugin){
//        var EMPIRE_GUN_MAX_CLIP_SIZE: NamespacedKey = NamespacedKey(plugin, "EMPIRE_GUN_MAX_CLIP_SIZE")
//        var EMPIRE_GUN_CURRENT_CLIP_SIZE: NamespacedKey = NamespacedKey(plugin, "EMPIRE_GUN_CURRENT_CLIP_SIZE")
//        var EMPIRE_GUN_DAMAGE: NamespacedKey = NamespacedKey(plugin, "EMPIRE_GUN_DAMAGE")
//        var EMPIRE_GUN_LENGTH: NamespacedKey = NamespacedKey(plugin, "EMPIRE_GUN_LENGTH")
//        var EMPIRE_GUN_COOLDOWN: NamespacedKey = NamespacedKey(plugin, "EMPIRE_GUN_COOLDOWN")
//        var RELOAD_BY: NamespacedKey = NamespacedKey(plugin, "RELOAD_BY")
//    }

}