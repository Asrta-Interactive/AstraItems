package com.makeevrserg.empireprojekt.npc.commands

import com.makeevrserg.empireprojekt.npc.NPCManager
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Commands : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player)
            return true


        if (args.isNotEmpty() && args[0].equals("tp", ignoreCase = true))
            teleportToNpc(sender, label, args)

        if (args.isNotEmpty() && args[0].equals("move", ignoreCase = true))
            moveNpcToPlayer(sender, label, args)
        if (args.isNotEmpty() && args[0].equals("skin", ignoreCase = true))
            setNpcSkin(sender, label, args)

        if (args.isNotEmpty() && args[0].equals("create", ignoreCase = true))
            createNPC(sender,  args)

        return true
    }

    private fun createNPC(sender: Player,args: Array<out String>){
        if (args.size != 2)
            return
        NPCManager.createNPC(args[1],sender.location.clone())
    }
    private fun setNpcSkin(sender: Player, label: String, args: Array<out String>) {
        if (args.size != 3)
            return
        if (NPCManager.abstractNPCByName.containsKey(args[1]))
            NPCManager.abstractNPCByName[args[1]]!!.setSkinByName(args[2])
    }

    private fun moveNpcToPlayer(sender: Player, label: String, args: Array<out String>) {
        if (args.size != 2)
            return
        if (NPCManager.abstractNPCByName.containsKey(args[1]))
            NPCManager.abstractNPCByName[args[1]]!!.setLocation(sender.location)
    }

    private fun teleportToNpc(sender: Player, label: String, args: Array<out String>) {

        if (args.size != 2)
            return
        if (NPCManager.abstractNPCByName.containsKey(args[1]))
            sender.teleport(NPCManager.abstractNPCByName[args[1]]!!.location)
    }
}