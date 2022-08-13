package com.astrainteractive.empire_items.empire_items.commands

import com.astrainteractive.astralibs.AstraLibs
import com.astrainteractive.astralibs.utils.registerCommand
import com.astrainteractive.astralibs.utils.registerTabCompleter
import com.astrainteractive.astralibs.utils.withEntry
import com.astrainteractive.empire_items.api.EmpireItemsAPI
import com.astrainteractive.empire_items.empire_items.events.api_events.model_engine.ModelEngineApi
import com.astrainteractive.empire_items.empire_items.util.EmpirePermissions
import com.astrainteractive.empire_items.empire_items.util.Translations
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player

class ModelEngine {
    private val transparent: Set<Material> = setOf(Material.AIR, Material.CAVE_AIR, Material.TALL_GRASS)


    val spawnmodel =
        AstraLibs.registerCommand("spawnmodel", permission = EmpirePermissions.spawnModel) { sender, args ->

            val id = args.firstOrNull() ?: run {
                sender.sendMessage(Translations.instance.wrongArgs)
                return@registerCommand
            }
            val empireMob = EmpireItemsAPI.ymlMobById[id] ?: run {
                sender.sendMessage(Translations.instance.mobNotExist)
                return@registerCommand
            }
            val world = args.getOrNull(1)?.let { Bukkit.getWorld(it)}
            val x = args.getOrNull(2)?.toDoubleOrNull()
            val y = args.getOrNull(3)?.toDoubleOrNull()
            val z = args.getOrNull(4)?.toDoubleOrNull()
            val location = x?.let { x -> y?.let { y -> z?.let { z -> Location(world, x, y, z) } } }



            val l = location ?: (sender as? Player)?.getTargetBlock(transparent, 64)?.getRelative(BlockFace.UP)?.location?:return@registerCommand
            println("Call ModelEngineApi.spawnMob")
            val spawned = ModelEngineApi.spawnMob(empireMob, l)
            if (spawned == null) {
                sender.sendMessage(Translations.instance.mobFailedToSpawn)
            }

        }
    val tabCompleter = AstraLibs.registerTabCompleter("spawnmodel") { sender, args ->
        val models = EmpireItemsAPI.ymlMobById.keys
        return@registerTabCompleter models.toList().withEntry(args.firstOrNull() ?: "")
    }
}