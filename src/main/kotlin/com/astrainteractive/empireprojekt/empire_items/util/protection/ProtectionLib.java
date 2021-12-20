package com.astrainteractive.empireprojekt.empire_items.util.protection;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

public class ProtectionLib {

    private final static Set<KProtectionCompatibility> compatibilities = new HashSet<>();

    public static void init(JavaPlugin plugin) {
        handleCompatibility("WorldGuard", plugin, KWorldGuardCompat::new);
    }

    public static boolean canBuild(Player player, Location target) {
        return compatibilities.stream().allMatch(compatibility -> compatibility.canBuild(player, target));
    }

    public static boolean canBreak(Player player, Location target) {
        return compatibilities.stream().allMatch(compatibility -> compatibility.canBreak(player, target));
    }

    private static void handleCompatibility(String pluginName, JavaPlugin mainPlugin, CompatibilityConstructor constructor) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
        if (plugin != null) {
            compatibilities.add(constructor.create(mainPlugin, plugin));
        }
    }

    @FunctionalInterface
    private interface CompatibilityConstructor {
        KProtectionCompatibility create(JavaPlugin mainPlugin, Plugin plugin);
    }

}
