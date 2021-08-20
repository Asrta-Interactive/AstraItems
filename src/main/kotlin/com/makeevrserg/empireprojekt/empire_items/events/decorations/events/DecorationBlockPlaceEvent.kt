package makeevrserg.empireprojekt.events.decorations.events

import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.empire_items.util.EmpirePermissions
import com.makeevrserg.empireprojekt.empirelibs.IEmpireListener
import com.makeevrserg.empireprojekt.empirelibs.getEmpireID
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.ItemFrame
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerInteractEntityEvent

class DecorationBlockPlaceEvent : IEmpireListener {

    @EventHandler
    fun blockPlaceEvent(e: BlockPlaceEvent) {
        val player = e.player
        val itemStack = player.inventory.itemInMainHand.clone()
        val itemId = itemStack.getEmpireID() ?: return
        val decoration = EmpirePlugin.empireItems.empireDecorations[itemId] ?: return
        val location = e.blockPlaced.location.clone()
        location.block.type = Material.AIR

        val itemFrame = location.world!!.spawnEntity(location, EntityType.ITEM_FRAME) as ItemFrame
        itemFrame.isFixed = true
        itemFrame.isVisible = false
        itemFrame.itemDropChance = 0.0f
        itemFrame.isCustomNameVisible = true
        itemFrame.isInvulnerable = true
        itemFrame.customName = ""
        itemFrame.boundingBox

        val meta = itemStack.itemMeta
        meta?.setDisplayName(null)
        itemStack.itemMeta = meta
        itemFrame.setItem(itemStack)
    }

    @EventHandler
    fun entityClickEvent(e: PlayerInteractEntityEvent) {
        val entity = e.rightClicked
        if (entity !is ItemFrame)
            return
        val itemFrame = entity as ItemFrame

        if (e.player.isSneaking) {
            if (!e.player.hasPermission(EmpirePermissions.FIXED_ITEM_FRAME))
                return
            itemFrame.isFixed = !itemFrame.isFixed
            itemFrame.isInvulnerable = !itemFrame.isInvulnerable
            e.isCancelled = true
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun frameBreakEvent(e: EntityDamageByEntityEvent) {
        val entity = e.entity
        if (entity !is ItemFrame)
            return
        val itemStack = entity.item
        val itemId = itemStack.getEmpireID() ?: return
        val decoration = EmpirePlugin.empireItems.empireDecorations[itemId] ?: return
        entity.remove()

    }

    override fun onDisable() {
        BlockPlaceEvent.getHandlerList().unregister(this)
        PlayerInteractEntityEvent.getHandlerList().unregister(this)
        EntityDamageByEntityEvent.getHandlerList().unregister(this)
        EntityDamageEvent.getHandlerList().unregister(this)

    }
}