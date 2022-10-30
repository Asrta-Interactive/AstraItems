package com.astrainteractive.empire_items.commands

import com.astrainteractive.empire_items.meg.api.EmpireModelEngineAPI
import com.astrainteractive.empire_items.modules.TranslationModule
import ru.astrainteractive.astralibs.AstraLibs
import ru.astrainteractive.astralibs.utils.registerCommand
import ru.astrainteractive.astralibs.utils.registerTabCompleter
import ru.astrainteractive.astralibs.utils.withEntry
import com.astrainteractive.empire_itemss.api.EmpireItemsAPI
import com.astrainteractive.empire_items.util.EmpirePermissions
import com.astrainteractive.empire_items.util.Translations
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player

class ModelEngine {
    private val transparent: Set<Material> = setOf(Material.AIR, Material.CAVE_AIR, Material.TALL_GRASS)
    private val translations:Translations
        get() = TranslationModule.value

    val spawnmodel =
        AstraLibs.registerCommand("spawnmodel", permission = EmpirePermissions.spawnModel) { sender, args ->

            val id = args.firstOrNull() ?: run {
                sender.sendMessage(translations.wrongArgs)
                return@registerCommand
            }
            val empireMob = EmpireItemsAPI.ymlMobById[id] ?: run {
                sender.sendMessage(translations.mobNotExist)
                return@registerCommand
            }
            val world = args.getOrNull(1)?.let { Bukkit.getWorld(it)}
            val x = args.getOrNull(2)?.toDoubleOrNull()
            val y = args.getOrNull(3)?.toDoubleOrNull()
            val z = args.getOrNull(4)?.toDoubleOrNull()
            val location = x?.let { x -> y?.let { y -> z?.let { z -> Location(world, x, y, z) } } }



            val l = location ?: (sender as? Player)?.getTargetBlock(transparent, 64)?.getRelative(BlockFace.UP)?.location?:return@registerCommand
            println("Call ModelEngineApi.spawnMob")
            val spawned = EmpireModelEngineAPI.spawnMob(empireMob, l)
            if (spawned == null) {
                sender.sendMessage(translations.mobFailedToSpawn)
            }

        }
    val tabCompleter = AstraLibs.registerTabCompleter("spawnmodel") { sender, args ->
        val models = EmpireItemsAPI.ymlMobById.keys
        return@registerTabCompleter models.toList().withEntry(args.firstOrNull() ?: "")
    }
}