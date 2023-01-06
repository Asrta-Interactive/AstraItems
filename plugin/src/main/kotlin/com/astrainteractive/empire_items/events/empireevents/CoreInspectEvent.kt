package com.astrainteractive.empire_items.events.empireevents

import com.astrainteractive.empire_itemss.api.utils.BukkitConstants
import net.coreprotect.CoreProtect
import net.coreprotect.CoreProtectAPI
import org.bukkit.ChatColor
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import ru.astrainteractive.astralibs.AstraLibs
import ru.astrainteractive.astralibs.events.DSLEvent
import ru.astrainteractive.astralibs.utils.HEX
import ru.astrainteractive.astralibs.utils.persistence.Persistence.getPersistentData
import ru.astrainteractive.astralibs.utils.persistence.Persistence.setPersistentDataType
import java.text.SimpleDateFormat
import java.util.*


class CoreInspectEvent{

    var coreProtect: CoreProtectAPI? = null
    val c: String
        get() = ChatColor.AQUA.toString()

    init {
        AstraLibs.instance.server.pluginManager.getPlugin("CoreProtect")?.let {
            coreProtect = (it as CoreProtect).api
        }
    }



    val onPlayerInteract = DSLEvent.event<PlayerInteractEvent>  { e ->
        coreProtect ?: return@event
        val itemStack = e.item?:return@event
        val itemMeta = itemStack.itemMeta?:return@event
        val time = e.item?.itemMeta?.getPersistentData(BukkitConstants.CORE_INSPECT) ?: return@event
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
            return@event
        val result = if (e.action==Action.LEFT_CLICK_AIR || e.action==Action.RIGHT_CLICK_AIR)
            coreProtect?.performLookup(time*60,null,null,null,null,null,20,e.player.location)
        else
            coreProtect?.blockLookup(e.clickedBlock, time * 60)

        if (result?.isEmpty() != false) {
            e.player.sendMessage("${c}[Лупа]: Тут ничего не происходило в течение ${time} минут")
            e.player.sendMessage("${c}[Лупа]: Чтобы увеличить или уменьшить время - ударьте левой/правой рукой по воздуху")
            return@event
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
}