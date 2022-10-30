package com.astrainteractive.empire_items.util.protection

import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin


abstract class KProtectionCompatibility(val mainPlugin: JavaPlugin, val plugin: Plugin) {

    /**
     * @param player Player looking to place a block
     * @param target Place where the player seeks to place a block
     * @return true if he can put the block
     */
    abstract fun canBuild(player: Player?, target: Location): Boolean

    /**
     * @param player Player looking to break a block
     * @param target Place where the player seeks to break a block
     * @return true if he can break the block
     */
    abstract fun canBreak(player: Player?, target: Location): Boolean
    abstract fun canExplode(player: Player?, target: Location): Boolean
    abstract fun canIgnite(player: Player?, target: Location): Boolean

}