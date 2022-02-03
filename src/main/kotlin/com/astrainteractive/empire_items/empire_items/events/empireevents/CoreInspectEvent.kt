package com.astrainteractive.empire_items.empire_items.events.empireevents

import com.astrainteractive.astralibs.AstraLibs
import com.astrainteractive.astralibs.HEX
import com.astrainteractive.astralibs.EventListener
import com.astrainteractive.astralibs.EventManager
import com.astrainteractive.empire_items.empire_items.api.utils.BukkitConstants
import com.astrainteractive.empire_items.empire_items.api.utils.getPersistentData
import com.astrainteractive.empire_items.empire_items.api.utils.setPersistentDataType
import net.coreprotect.CoreProtect
import net.coreprotect.CoreProtectAPI
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import java.text.SimpleDateFormat
import java.util.*


class CoreInspectEvent : EventListener {

    var coreProtect: CoreProtectAPI? = null
    val c: String
        get() = ChatColor.AQUA.toString()

    override fun onEnable(manager: EventManager): EventListener {
        AstraLibs.instance.server.pluginManager.getPlugin("CoreProtect")?.let {
            coreProtect = (it as CoreProtect).api
        }
        return super.onEnable(manager)

    }



    @EventHandler
    private fun onPlayerInteract(e: PlayerInteractEvent) {
        coreProtect ?: return
        val itemStack = e.item?:return
        val itemMeta = itemStack.itemMeta?:return
        val time = e.item?.itemMeta?.getPersistentData(BukkitConstants.CORE_INSPECT) ?: return
        if (!e.player.isSneaking) {
            if (e.action == Action.LEFT_CLICK_AIR) {
                itemMeta.setPersistentDataType(BukkitConstants.CORE_INSPECT, time + 5)
                e.player.sendMessage("${c}[Лупа]: Время просмотра теперь ${time+5} минут")
            } else if (e.action == Action.RIGHT_CLICK_AIR && time > 5) {
                itemMeta.setPersistentDataType(BukkitConstants.CORE_INSPECT, time - 5)
                e.player.sendMessage("${c}[Лупа]:Время просмотра теперь ${time-5} минут")
            }
            itemStack.itemMeta = itemMeta
        }


        if (!e.player.isSneaking)
            return
        val result = if (e.action==Action.LEFT_CLICK_AIR || e.action==Action.RIGHT_CLICK_AIR)
            coreProtect?.performLookup(time*60,null,null,null,null,null,20,e.player.location)
        else
            coreProtect?.blockLookup(e.clickedBlock, time * 60)

        if (result?.isEmpty() != false) {
            e.player.sendMessage("${c}[Лупа]: Тут ничего не происходило в течение ${time} минут")
            e.player.sendMessage("${c}[Лупа]: Чтобы увеличить или уменьшить время - ударьте левой/правой рукой по воздуху")
            return
        }
        val last = if (result.size<5) result.size-1 else 5
        result.takeLast(last).forEach { res ->
            val r = coreProtect?.parseResult(res)
            r ?: return@forEach
            e.player.sendMessage(
                "${c}[Лупа]: #42f557${r.player} #429ef5-> #f54b42${r.actionString} #429ef5${
                    SimpleDateFormat(
                        "dd-M HH:mm:ss"
                    ).format(Date(r.timestamp))
                }".HEX()
            )
        }
    }

    override fun onDisable() {
        PlayerInteractEvent.getHandlerList().unregister(this)
    }
}