package com.makeevrserg.empireprojekt.npcs

import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.EmpirePlugin.Companion.instance
import com.makeevrserg.empireprojekt.EmpirePlugin.Companion.translations
import com.makeevrserg.empireprojekt.empirelibs.EmpireUtils

import net.minecraft.server.level.EntityPlayer
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

class NPCCommandHandler : CommandExecutor {
    init {
        EmpirePlugin.instance.getCommand("emnpc")!!.setExecutor(this)
    }

    private fun Location(e: EntityPlayer?): Location? {
        e ?: return null
        return Location(e.world.world, e.locX(), e.locY(), e.locZ())
    }

    private fun getTargetNPC(entity: Entity): EmpireNPC? {
        val treshold = 1.0
        var target: EmpireNPC? = null
        for (other in NPCManager.NPC) {
            val entityLoc = other.location
            val targetLoc = target?.location
            val n = entityLoc.toVector().subtract(entity.location.toVector())
            if (entity.location.direction.normalize().crossProduct(n).lengthSquared() < treshold
                && n.normalize().dot(entity.location.direction.normalize()) >= 0
            ) {
                if (targetLoc == null || targetLoc.distanceSquared(entity.location) > entityLoc.distanceSquared(
                        entity.location
                    )
                ) {
                    target = other

                }
            }
        }

        return target

    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player)
            return false
        when (args.size) {
            0 -> return false
            1 -> {
                if (args[0].equals("create", ignoreCase = true))

                    sender.sendMessage(translations.NPC_NOT_WRITTEN_ID)
                if (args[0].equals("tp", ignoreCase = true))
                    sender.sendMessage(translations.NPC_NOT_WRITTEN_ID)
                if (args[0].equals("move", ignoreCase = true))
                    NPCManager.relocateNPC(sender)
                if (args[0].equals("delete", ignoreCase = true))
                    NPCManager.removeNPC(sender)
                if (args[0].equals("changeskin", ignoreCase = true))
                    sender.sendMessage(translations.NPC_NOT_WRITTEN_ID)

                if (args[0].equals("select", ignoreCase = true)) {
                    val target = getTargetNPC(sender)
                    if (target != null) {
                        NPCManager.selectedNPC[sender] = target
                        sender.sendMessage(translations.NPC_FOUND_RAYCAST + " ${target.name}")
                    } else
                        sender.sendMessage(translations.NPC_NOT_FOUND_RAYCAST)
                }
            }
            2 -> {
                if (args[0].equals("create", ignoreCase = true)) {
                    EmpireUtils.EmpireRunnable {
                        NPCManager.createNPC(sender, args[1])
                        sender.sendMessage(translations.NPC_CREATED)
                    }.runTaskAsynchronously(instance)
                }
                if (args[0].equals("tp", ignoreCase = true))
                    sender.teleport(NPCManager.NPCMap[args[1]]?.location ?: return true )

                if (args[0].equals("move", ignoreCase = true))
                    NPCManager.relocateNPC(sender, args[1])

                if (args[0].equals("delete", ignoreCase = true))
                    NPCManager.removeNPC(sender, args[1])

                if (args[0].equals("changeskin", ignoreCase = true))
                    NPCManager.changeSkin(sender, args[1])

            }
            3 -> {
                if (args[0].equals("create", ignoreCase = true)) {
                    EmpireUtils.EmpireRunnable {
                        NPCManager.createNPC(sender, args[1], args[2])
                        sender.sendMessage(translations.NPC_CREATED)
                    }.runTaskAsynchronously(instance)
                }

                if (args[0].equals("changeskin", ignoreCase = true))
                    NPCManager.changeSkin(sender,args[2],args[1])


            }
        }
        return false
    }
}