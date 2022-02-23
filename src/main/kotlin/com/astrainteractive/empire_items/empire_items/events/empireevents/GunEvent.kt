package com.astrainteractive.empire_items.empire_items.events.empireevents

import com.astrainteractive.astralibs.AstraLibs
import com.astrainteractive.astralibs.EventListener
import com.astrainteractive.empire_items.api.items.data.Gun
import com.astrainteractive.empire_items.api.items.data.ItemApi
import com.astrainteractive.empire_items.api.items.data.ItemApi.getAstraID
import com.astrainteractive.empire_items.api.items.data.ItemApi.toAstraItemOrItem
import com.astrainteractive.empire_items.api.utils.BukkitConstants
import com.astrainteractive.empire_items.api.utils.getPersistentData
import com.astrainteractive.empire_items.api.utils.setPersistentDataType
import com.astrainteractive.empire_items.empire_items.util.protection.KProtectionLib
import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import com.destroystokyo.paper.ParticleBuilder
import org.bukkit.*
import org.bukkit.block.BlockFace
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.player.*
import org.bukkit.inventory.EntityEquipment
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import java.lang.IllegalArgumentException
import java.lang.NumberFormatException

class GunEvent : EventListener {

    private var protocolManager: ProtocolManager? = null

    init {
        if (AstraLibs.instance.server.pluginManager.isPluginEnabled("protocollib"))
            protocolManager = ProtocolLibrary.getProtocolManager()
    }


    private val lastShootMap: MutableMap<String, Long> = mutableMapOf()

    private fun canShoot(player: Player, gun: Gun): Boolean {
        val lastShoot = lastShootMap[player.name]
        return when {
            System.currentTimeMillis().minus(lastShoot ?: 0) >= gun.cooldown ?: 0 -> {
                lastShootMap[player.name] = System.currentTimeMillis()
                true
            }
            lastShoot == null -> {
                lastShootMap[player.name] = System.currentTimeMillis()
                true
            }
            else -> false
        }
    }

    private fun reloadGun(player: Player, itemStack: ItemStack, gun: Gun) {
        val itemMeta = itemStack.itemMeta
        val currentClipSize = itemMeta.getPersistentData(BukkitConstants.CLIP_SIZE) ?: return
        if (currentClipSize == gun.clipSize) {
            player.world.playSound(player.location, gun.reloadSound ?: "", 1.0f, 1.0f)
            return
        }
        val reloadBy = gun.reload.toAstraItemOrItem() ?: return
        if (player.inventory.containsAtLeast(reloadBy, 1)) {
            player.inventory.removeItem(reloadBy)
            player.world.playSound(player.location, gun.reloadSound ?: "", 1.0f, 1.0f)
            itemMeta.setPersistentDataType(BukkitConstants.CLIP_SIZE, gun.clipSize)
        }
        itemStack.itemMeta = itemMeta
    }

    private fun setRecoil(player: Player, recoil: Double) {
        fun setProtocolPitch(p: Player, recoil: Double) {
            val yawPacket = protocolManager?.createPacket(PacketType.Play.Server.POSITION, false) ?: return
            yawPacket.modifier.writeDefaults()
            yawPacket.doubles.write(0, p.location.x)
            yawPacket.doubles.write(1, p.location.y)
            yawPacket.doubles.write(2, p.location.z)
            yawPacket.float.write(0, p.location.yaw)
            yawPacket.float.write(1, p.location.pitch - recoil.toFloat())
            protocolManager?.sendServerPacket(p, yawPacket) ?: return
        }
        if (recoil == 0.0)
            return

        if (player.location.block.getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).type == Material.AIR)
            return
        if (AstraLibs.instance.server.pluginManager.isPluginEnabled("protocollib")) {
            setProtocolPitch(player, recoil)
            return
        }

        val loc = player.location.clone()
        loc.pitch -= recoil.toFloat()
        player.teleport(loc)
    }


    //#FFFFFF
    private fun rgbToColor(color: String): Color {
        return try {
            Color.fromRGB(Integer.decode(color.replace("#", "0x")))
        } catch (e: NumberFormatException) {
            Color.BLACK
        } catch (e: IllegalArgumentException) {
            Color.BLACK
        }
    }

    @EventHandler
    fun playerInteractEvent(e: PlayerInteractEvent) {

        val itemStack = e.item ?: return
        val id = itemStack.getAstraID()
        val gunInfo = ItemApi.getItemInfo(id)?.gun ?: return
        val player = e.player
        if (e.action == Action.LEFT_CLICK_AIR || e.action == Action.LEFT_CLICK_BLOCK) {
            reloadGun(player, itemStack, gunInfo)
            return
        }
        val currentClipSize = itemStack.itemMeta.getPersistentData(BukkitConstants.CLIP_SIZE)
        if (currentClipSize == 0) {
            player.world.playSound(player.location, gunInfo.noAmmoSound ?: "", 1.0f, 1.0f)
            return
        }

        if (!canShoot(player, gunInfo))
            return
        player.world.playSound(player.location, gunInfo.shootSound ?: "", 1.0f, 1.0f)
        var itemMeta = itemStack.itemMeta
        if (currentClipSize != null)
            itemMeta.setPersistentDataType(BukkitConstants.CLIP_SIZE, currentClipSize.minus(1))
        itemStack.itemMeta = itemMeta
        var l = player.location.add(0.0, 1.3, 0.0)
        if (player.isSneaking)
            l = l.add(0.0, -0.2, 0.0)

        if (gunInfo.recoil != null)
            setRecoil(player, gunInfo.recoil)

        val r = if (player.isSneaking) (gunInfo.radiusSneak ?: gunInfo.radius * 2) else gunInfo.radius


        for (i in 0 until gunInfo.bulletTrace) {
            ParticleBuilder(Particle.REDSTONE)
                .count(20)
                .force(true)
                .extra(0.06)
                .data(null)
                .color(rgbToColor(gunInfo.color ?: "#000000"))
                .location(l.world ?: return, l.x, l.y, l.z)
                .spawn()
            l =
                l.add(
                    l.direction.x,
                    l.direction.y - i / (gunInfo.bulletTrace * (gunInfo.bulletWeight ?: 1.0)),
                    l.direction.z
                )

            if (!l.block.isPassable)
                break

            for (ent: Entity in getEntityByLocation(l, r))
                if (ent is LivingEntity && ent != player) {
                    val damage = (1 - i / gunInfo.bulletTrace) * gunInfo.damage
                    ent.damage(damage, player)
                }
        }
        if (gunInfo.explosion != null && KProtectionLib.canExplode(null, l))
            GrenadeEvent.generateExplosion(l, gunInfo.explosion.toDouble())

    }


    fun modifersAmount(itemStack: ItemStack?, slot: EquipmentSlot): Double {
        var amount = 0.0
        itemStack?.itemMeta?.attributeModifiers?.forEach { a, am ->
            if (am?.slot != slot)
                return@forEach
            amount += am.amount
        }
        return amount
    }

    fun getEntityArmor(e: EntityEquipment?): Double {
        e ?: return 0.0
        return listOf(
            modifersAmount(e.helmet, EquipmentSlot.HEAD),
            modifersAmount(e.chestplate, EquipmentSlot.CHEST),
            modifersAmount(e.leggings, EquipmentSlot.LEGS),
            modifersAmount(e.boots, EquipmentSlot.FEET),
            modifersAmount(e.itemInMainHand, EquipmentSlot.HAND),
            modifersAmount(e.itemInOffHand, EquipmentSlot.OFF_HAND)
        ).sum()
    }

    private fun getEntityByLocation(loc: Location, r: Double): MutableList<Entity> {
        val entities: MutableList<Entity> = mutableListOf()
        loc.world ?: return mutableListOf()
        for (e in loc.world!!.entities)
            if (e.location.distanceSquared(loc) <= r)
                entities.add(e)
        return entities
    }

    override fun onDisable() {
        PlayerInteractEvent.getHandlerList().unregister(this)
        PlayerMoveEvent.getHandlerList().unregister(this)
        PlayerToggleSneakEvent.getHandlerList().unregister(this)
        PlayerItemHeldEvent.getHandlerList().unregister(this)
        PlayerSwapHandItemsEvent.getHandlerList().unregister(this)
        PlayerDropItemEvent.getHandlerList().unregister(this)
    }
}
