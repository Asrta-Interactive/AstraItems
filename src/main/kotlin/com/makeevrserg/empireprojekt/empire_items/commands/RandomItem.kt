package makeevrserg.empireprojekt.commands

import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.empire_items.util.EmpirePermissions
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class RandomItem:CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(!sender.hasPermission(EmpirePermissions.GET_RANDOM_ITEM))
            return true
        if (sender !is Player)
            return true
        val player = sender as Player
        if (args.isEmpty())
            return true
        player.inventory.addItem(EmpirePlugin.instance.randomItems.getItem(args[0]))
        return false
    }
}