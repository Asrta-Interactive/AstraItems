package com.astrainteractive.empireprojekt.empire_items.api.items.data

import com.astrainteractive.empireprojekt.empire_items.api.items.data.AstraItem.Companion.getIntOrNull
import com.astrainteractive.empireprojekt.empire_items.api.utils.getDoubleOrNull
import org.bukkit.configuration.ConfigurationSection

data class Gun(
    val cooldown:Int?,
    val recoil:Double?,
    val clipSize:Int?,
    val bulletWeight:Double?,
    val bulletTrace:Int,
    val color:String?,
    val damage:Double,
    val reload:String?,
    val reloadSound:String?,
    val fullSound:String?,
    val shootSound:String?,
    val noAmmoSound:String?,
    val radius:Double,
    val radiusSneak:Double?,
    val explosion:Int?
){
    companion object{
        fun getGun(s:ConfigurationSection?): Gun? {
            s?:return null
            val cooldown = s.getIntOrNull("cooldown")
            val recoil = s.getDoubleOrNull("recoil")
            val clipSize = s.getIntOrNull("clipSize")
            val bulletWeight = s.getDoubleOrNull("bulletWeight")
            val bulletTrace = s.getInt("bulletTrace",100)
            val color = s.getString("color")
            val damage = s.getDoubleOrNull("damage")?:return null
            val reload = s.getString("reload")
            val reloadSound = s.getString("reloadSound")
            val fullSound = s.getString("fullSound")
            val shootSound = s.getString("shootSound")
            val noAmmoSound = s.getString("noAmmoSound")
            val radius = s.getDouble("radius",1.0)
            val radiusSneak = s.getDoubleOrNull("radiusSneak")
            val explosion = s.getIntOrNull("explosion")
            return Gun(
                cooldown = cooldown,
                recoil = recoil,
                clipSize = clipSize,
                bulletWeight = bulletWeight,
                bulletTrace = bulletTrace,
                color = color,
                damage = damage,
                reload = reload,
                reloadSound = reloadSound,
                fullSound = fullSound,
                shootSound = shootSound,
                noAmmoSound = noAmmoSound,
                radius =  radius,
                radiusSneak = radiusSneak,
                explosion = explosion
            )
        }
    }
}
