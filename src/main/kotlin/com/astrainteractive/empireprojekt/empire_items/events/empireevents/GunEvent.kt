//package com.makeevrserg.empireprojekt.empire_items.events.empireevents
//
//import com.comphenix.protocol.PacketType
//import com.comphenix.protocol.ProtocolLibrary
//import com.comphenix.protocol.ProtocolManager
//import com.destroystokyo.paper.ParticleBuilder
//import com.makeevrserg.empireprojekt.EmpirePlugin
//import com.makeevrserg.empireprojekt.EmpirePlugin.Companion.instance
//import com.makeevrserg.empireprojekt.items.EmpireGun
//import com.makeevrserg.empireprojekt.empirelibs.IEmpireListener
//import org.bukkit.*
//import org.bukkit.block.BlockFace
//import org.bukkit.entity.Entity
//import org.bukkit.entity.LivingEntity
//import org.bukkit.entity.Player
//import org.bukkit.event.EventHandler
//import org.bukkit.event.Listener
//import org.bukkit.event.block.Action
//import org.bukkit.event.player.*
//import org.bukkit.inventory.ItemStack
//import org.bukkit.inventory.meta.ItemMeta
//import org.bukkit.persistence.PersistentDataContainer
//import org.bukkit.persistence.PersistentDataType
//import org.bukkit.potion.PotionEffect
//import org.bukkit.potion.PotionEffectType
//import java.lang.IllegalArgumentException
//import java.lang.NumberFormatException
//
//class GunEvent : IEmpireListener {
//
//    private var protocolManager: ProtocolManager? = null
//
//    init {
//        if (instance.server.pluginManager.isPluginEnabled("protocollib"))
//            protocolManager = ProtocolLibrary.getProtocolManager()
//
//
//    }
//
//    private fun getEmpireID(itemMeta: ItemMeta?): String? {
//        return itemMeta?.persistentDataContainer?.get(EmpirePlugin.empireConstants.empireID, PersistentDataType.STRING)
//    }
//    private fun isAllowedToShoot(player: Player, cd: Double): Boolean {
//        if (!lastShoot.containsKey(player)) {
//            lastShoot[player] = System.currentTimeMillis()
//            return true
//        }
//        val lastTimeShoot = lastShoot[player]!!
//
//        if (System.currentTimeMillis().minus(lastTimeShoot) / 1000.0 < cd)
//            return false
//        lastShoot[player] = System.currentTimeMillis()
//        return true
//    }
//
//    private fun isCancelled(p: Player, item: ItemStack? = null): Boolean {
//
//        val idMainHand = getEmpireID(p.inventory.itemInMainHand.itemMeta)
//        val idOffHand = getEmpireID(p.inventory.itemInOffHand.itemMeta)
//        val dropItem = getEmpireID(item?.itemMeta)
//        EmpirePlugin.empireItems.empireGuns[idMainHand] ?: EmpirePlugin.empireItems.empireGuns[idOffHand] ?:EmpirePlugin.empireItems.empireGuns[dropItem]?: return false
//        if (p.isSneaking)
//            return true
//        return false
//
//    }
//
//    @EventHandler
//    fun itemDropEvent(e: PlayerDropItemEvent) {
//        e.isCancelled = isCancelled(e.player, e.itemDrop.itemStack)
//    }
//
//    @EventHandler
//    fun itemSwapEvent(e: PlayerSwapHandItemsEvent) {
//        e.isCancelled = isCancelled(e.player)
//
//    }
//
//    @EventHandler
//    fun itemHeldEvent(e: PlayerItemHeldEvent) {
//        e.isCancelled = isCancelled(e.player)
//    }
//
//    @EventHandler
//    fun onPlayerShift(e: PlayerToggleSneakEvent) {
//
//        val p = e.player
//        val mainHandItem = p.inventory.itemInMainHand
//        val mainHandID = getEmpireID(mainHandItem.itemMeta) ?: return
//        val offHandID = getEmpireID(p.inventory.itemInOffHand.itemMeta)?:return
//
//        val empGun: EmpireGun =
//            EmpirePlugin.empireItems.empireGuns[mainHandID] ?: EmpirePlugin.empireItems.empireGuns[offHandID] ?: return
//
//        if (e.isSneaking && !p.hasPotionEffect(PotionEffectType.SLOW)) {
//            p.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 10000, 255, true))
//            if (p.inventory.itemInOffHand.type == Material.AIR) {
//                val aimItem = EmpirePlugin.empireItems.empireItems[empGun.crosshair ?: return] ?: return
//                p.inventory.setItemInOffHand(mainHandItem)
//                p.inventory.setItemInMainHand(aimItem)
//            }
//        } else if (!e.isSneaking && p.hasPotionEffect(PotionEffectType.SLOW) && p.getPotionEffect(PotionEffectType.SLOW)!!.amplifier == 255) {
//            p.removePotionEffect(PotionEffectType.SLOW)
//            val offHandGun = EmpirePlugin.empireItems.empireGuns[offHandID] ?: return
//
//            offHandGun.crosshair ?: return
//            if (mainHandItem != EmpirePlugin.empireItems.empireItems[offHandGun.crosshair!!] ?: return)
//                return
//
//            p.inventory.setItemInMainHand(p.inventory.itemInOffHand)
//            p.inventory.setItemInOffHand(null)
//        }
//
//
//    }
//
//
//
//    private val lastShoot: MutableMap<Player, Long> = mutableMapOf()
//
//
//    private fun reloadGun(player: Player, dataContainer: PersistentDataContainer, gun: EmpireGun) {
//        val currentClipSize =
//            dataContainer.get(EmpirePlugin.empireConstants.EMPIRE_GUN_CURRENT_CLIP_SIZE, PersistentDataType.INTEGER)
//        if (currentClipSize == gun.clipSize) {
//            player.world.playSound(player.location, gun.reloadSound, 1.0f, 1.0f)
//            return
//        }
//        if (player.inventory.containsAtLeast(EmpirePlugin.empireItems.empireItems[gun.reloadBy], 1)) {
//
//            player.inventory.removeItem(EmpirePlugin.empireItems.empireItems[gun.reloadBy]!!)
//            player.world.playSound(player.location, gun.reloadSound, 1.0f, 1.0f)
//            dataContainer.set(
//                EmpirePlugin.empireConstants.EMPIRE_GUN_CURRENT_CLIP_SIZE,
//                PersistentDataType.INTEGER,
//                gun.clipSize
//            )
//        }
//    }
//
//    private fun setRecoil(player: Player, recoil: Double) {
//        fun setProtocolPitch(p: Player, recoil: Double) {
//            val yawPacket = protocolManager?.createPacket(PacketType.Play.Server.POSITION, false) ?: return
//            yawPacket.modifier.writeDefaults()
//            yawPacket.doubles.write(0, p.location.x)
//            yawPacket.doubles.write(1, p.location.y)
//            yawPacket.doubles.write(2, p.location.z)
//            yawPacket.float.write(0, p.location.yaw)
//            yawPacket.float.write(1, p.location.pitch - recoil.toFloat())
//            protocolManager?.sendServerPacket(p, yawPacket) ?: return
//        }
//        if (recoil==0.0)
//            return
//
//        if (player.location.block.getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).type == Material.AIR)
//            return
//        if (instance.server.pluginManager.isPluginEnabled("protocollib")) {
//            setProtocolPitch(player, recoil)
//            return
//        }
//
//        val loc = player.location.clone()
//        loc.pitch -= recoil.toFloat()
//        player.teleport(loc)
//    }
//
//
//    //#FFFFFF
//    private fun rgbToColor(color: String): Color {
//        return try {
//            Color.fromRGB(Integer.decode(color.replace("#", "0x")))
//        } catch (e: NumberFormatException) {
//            Color.BLACK
//        } catch (e: IllegalArgumentException) {
//            Color.BLACK
//        }
//    }
//
//    @EventHandler
//    fun playerInteractEvent(e: PlayerInteractEvent) {
//
//        val itemStack = e.item ?: return
//        val itemMeta = itemStack.itemMeta ?: return
//        val id =
//            itemMeta.persistentDataContainer.get(EmpirePlugin.empireConstants.empireID, PersistentDataType.STRING)
//                ?: return
//        val empGun: EmpireGun = EmpirePlugin.empireItems.empireGuns[id] ?: return
//        val player = e.player
//        if (e.action == Action.LEFT_CLICK_AIR || e.action == Action.LEFT_CLICK_BLOCK) {
//            reloadGun(player, itemMeta.persistentDataContainer, empGun)
//            itemStack.itemMeta = itemMeta
//            return
//        }
//
//
//        val currentClipSize =
//            itemMeta.persistentDataContainer.get(
//                EmpirePlugin.empireConstants.EMPIRE_GUN_CURRENT_CLIP_SIZE,
//                PersistentDataType.INTEGER
//            ) ?: return
//        if (currentClipSize == 0) {
//            player.world.playSound(player.location, empGun.noAmmoSound, 1.0f, 1.0f)
//            return
//        }
//
//        if (!isAllowedToShoot(player,empGun.gunCooldown))
//            return
//        player.world.playSound(player.location, empGun.shootSound, 1.0f, 1.0f)
//        itemMeta.persistentDataContainer.set(
//            EmpirePlugin.empireConstants.EMPIRE_GUN_CURRENT_CLIP_SIZE,
//            PersistentDataType.INTEGER,
//            currentClipSize - 1
//        )
//        itemStack.itemMeta = itemMeta
//        var l = player.location.add(0.0, 1.3, 0.0)
//        if (player.isSneaking)
//            l = l.add(0.0, -0.2, 0.0)
//
//        setRecoil(player, empGun.gunRecoil)
//
//
//        var r = 2.0
//        if (player.isSneaking)
//            r *= 2
//
//        for (i in 0 until empGun.gunLength) {
//            ParticleBuilder(Particle.REDSTONE)
//                .count(20)
//                .force(false)
//                .extra(0.06)
//                .data(null)
//                .color(rgbToColor(empGun.bulletColor))
//                .location(l.world?:return, l.x, l.y, l.z)
//                .spawn()
//
//            l =
//                l.add(l.direction.x, l.direction.y - i / (empGun.gunLength * empGun.gunBulletWeight), l.direction.z)
//
//            if (!l.block.isPassable)
//                break
//
//            for (ent: Entity in getEntityByLocation(l, r))
//                if (ent is LivingEntity && ent != player)
//                    ent.damage(
//                        (1 - i / empGun.gunLength) * empGun.gunDamage
//                    )
//
//
//        }
//        if (empGun.generateExplosion != null && GrenadeEvent.allowExplosion(instance, l))
//            GrenadeEvent.generateExplosion(l, empGun.generateExplosion!!.toDouble())
//
//    }
//
//    private fun getEntityByLocation(loc: Location, r: Double): MutableList<Entity> {
//        val entities: MutableList<Entity> = mutableListOf()
//        loc.world?:return mutableListOf()
//        for (e in loc.world!!.entities)
//            if (e.location.distanceSquared(loc) <= r)
//                entities.add(e)
//        return entities
//    }
//
//    override fun onDisable() {
//        PlayerInteractEvent.getHandlerList().unregister(this)
//        PlayerMoveEvent.getHandlerList().unregister(this)
//        PlayerToggleSneakEvent.getHandlerList().unregister(this)
//        PlayerItemHeldEvent.getHandlerList().unregister(this)
//        PlayerSwapHandItemsEvent.getHandlerList().unregister(this)
//        PlayerDropItemEvent.getHandlerList().unregister(this)
//    }
//}
