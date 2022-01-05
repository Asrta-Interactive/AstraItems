package com.astrainteractive.empire_items.empire_items.util.protection

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
/**
 * Original code provided by Oraxen Th0rgal https://github.com/oraxen/protectionlib
 * @author Th0rgal
 */
class KProtectionLib {

    companion object {
        private val compatibilities: MutableSet<KProtectionCompatibility> = HashSet()

        fun init(plugin: JavaPlugin) {
            handleCompatibility("WorldGuard", plugin) { _mainPlugin: JavaPlugin?, _plugin: Plugin? ->
                KWorldGuardCompat(_mainPlugin, _plugin)
            }
        }


        private fun handleCompatibility(
            pluginName: String,
            mainPlugin: JavaPlugin,
            constructor: CompatibilityConstructor
        ) {
            val plugin = Bukkit.getPluginManager().getPlugin(pluginName)
            constructor.create(mainPlugin, plugin)?.let { compatibilities.add(it) }
        }

        private fun canAny(
            canAny: (KProtectionCompatibility, Player?, Location) -> Boolean,
            player: Player?,
            target: Location
        ): Boolean = if (!compatibilities.isNullOrEmpty())
            compatibilities.stream()
                .allMatch { compatibility: KProtectionCompatibility -> canAny.invoke(compatibility, player, target) }
        else true

        fun canBuild(player: Player?, target: Location): Boolean =
            canAny(KProtectionCompatibility::canBuild, player, target)

        fun canBreak(player: Player?, target: Location): Boolean =
            canAny(KProtectionCompatibility::canBreak, player, target)

        fun canExplode(player: Player?, target: Location): Boolean =
            canAny(KProtectionCompatibility::canExplode, player, target)

        fun canIgnite(player: Player?, target: Location): Boolean =
            canAny(KProtectionCompatibility::canIgnite, player, target)

    }


    fun interface CompatibilityConstructor {
        fun create(mainPlugin: JavaPlugin?, plugin: Plugin?): KProtectionCompatibility?
    }
}