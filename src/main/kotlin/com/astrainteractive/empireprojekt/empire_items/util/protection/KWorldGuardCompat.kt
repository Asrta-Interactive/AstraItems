package com.astrainteractive.empireprojekt.empire_items.util.protection

import com.astrainteractive.empireprojekt.EmpirePlugin
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldguard.LocalPlayer
import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.bukkit.WorldGuardPlugin
import com.sk89q.worldguard.protection.flags.Flag
import com.sk89q.worldguard.protection.flags.Flags
import com.sk89q.worldguard.protection.flags.StateFlag
import com.sk89q.worldguard.protection.regions.RegionContainer
import com.sk89q.worldguard.protection.regions.RegionQuery
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin

class KWorldGuardCompat(mainPlugin: JavaPlugin?, plugin: Plugin?) :
    KProtectionCompatibility(mainPlugin!!, plugin!!) {
    private val worldGuard: WorldGuard = WorldGuard.getInstance()
    private val regionContainer: RegionContainer = worldGuard.platform.regionContainer

    /**
     * @param player Player looking to place a block
     * @param target Place where the player seeks to place a block
     * @return true if he can put the block
     */
    override fun canBuild(player: Player?, target: Location?): Boolean =
        checkFlag(player, target, Flags.BLOCK_PLACE)

    /**
     * @param player Player looking to break a block
     * @param target Place where the player seeks to break a block
     * @return true if he can break the block
     */
    override fun canBreak(player: Player?, target: Location?): Boolean =
        checkFlag(player, target, Flags.BLOCK_BREAK)

    override fun canExplode(player: Player?, target: Location?): Boolean =
        checkFlag(player, target, Flags.CREEPER_EXPLOSION, Flags.OTHER_EXPLOSION)

    override fun canIgnite(player: Player?, target: Location?): Boolean =
        checkFlag(player, target, Flags.LIGHTER, Flags.FIRE_SPREAD)


    private fun checkFlag(player: Player?, target: Location?, vararg flag: StateFlag?): Boolean {
        val localPlayer:LocalPlayer? = player?.let {
            (plugin as WorldGuardPlugin).wrapPlayer(player)
        }
        val bypass = player?.let{
            worldGuard.platform
                .sessionManager
                .hasBypass(localPlayer, BukkitAdapter.adapt(it.world))
        }?:false
        return (regionContainer.createQuery().testState(BukkitAdapter.adapt(target), localPlayer, *flag) || bypass)
    }


}