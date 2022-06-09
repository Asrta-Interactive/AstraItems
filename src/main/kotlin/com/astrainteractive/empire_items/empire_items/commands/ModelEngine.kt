package com.astrainteractive.empire_items.empire_items.commands

import com.astrainteractive.astralibs.AstraLibs
import com.astrainteractive.astralibs.registerCommand
import com.astrainteractive.astralibs.registerTabCompleter
import com.astrainteractive.astralibs.withEntry
import com.astrainteractive.empire_items.api.EmpireItemsAPI
import com.astrainteractive.empire_items.api.mobs.MobApi
import com.astrainteractive.empire_items.empire_items.util.EmpirePermissions
import com.astrainteractive.empire_items.empire_items.util.Translations
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player

class ModelEngine {
    private val transparent: Set<Material> = setOf(Material.AIR, Material.CAVE_AIR, Material.TALL_GRASS)


    val spawnmodel =
        AstraLibs.registerCommand("spawnmodel", permission = EmpirePermissions.spawnModel) { sender, args ->
            if (sender !is Player) {
                sender.sendMessage(Translations.instance.notPlayer)
                return@registerCommand
            }
            val id = args.firstOrNull()
            if (id == null) {
                sender.sendMessage(Translations.instance.wrongArgs)
                return@registerCommand
            }
            val empireMob = EmpireItemsAPI.ymlMobById[id]
            if (empireMob == null) {
                sender.sendMessage(Translations.instance.mobNotExist)
                return@registerCommand
            }
            sender as Player
            val block = sender.getTargetBlock(transparent, 64).getRelative(BlockFace.UP)
            val spawned = MobApi.spawnMob(empireMob, block.location)
            if (spawned == null) {
                sender.sendMessage(Translations.instance.mobFailedToSpawn)
            }

        }
    val tabCompleter = AstraLibs.registerTabCompleter("spawnmodel") { sender, args ->
        val models = EmpireItemsAPI.ymlMobById.keys
        return@registerTabCompleter models.toList().withEntry(args.firstOrNull() ?: "")
    }
}