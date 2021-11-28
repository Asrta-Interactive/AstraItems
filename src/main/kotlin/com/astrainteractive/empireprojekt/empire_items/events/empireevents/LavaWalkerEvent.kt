package com.astrainteractive.empireprojekt.empire_items.events.empireevents

import com.astrainteractive.astralibs.IAstraListener
import com.astrainteractive.empireprojekt.empire_items.api.utils.BukkitConstants
import com.astrainteractive.empireprojekt.empire_items.api.utils.getPersistentData
import com.astrainteractive.empireprojekt.empire_items.api.utils.hasPersistentData
import net.minecraft.world.level.GeneratorAccess
import net.minecraft.world.level.block.state.IBlockData
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.MultipleFacing
import org.bukkit.craftbukkit.v1_17_R1.block.CraftBlock
import org.bukkit.craftbukkit.v1_17_R1.block.data.CraftBlockData
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType

class LavaWalkerEvent : IAstraListener {



    override fun onDisable() {
        PlayerMoveEvent.getHandlerList().unregister(this)
        EntityDamageEvent.getHandlerList().unregister(this)
    }

    fun Block.setTypeFast(type: Material) {
        val craftBlock = (this as CraftBlock)
        val generatorAccess = (craftBlock.craftWorld.handle as GeneratorAccess)
        val old: IBlockData = generatorAccess.getType(craftBlock.position)
        val craftBlockData = (type.createBlockData() as CraftBlockData)
        generatorAccess.setTypeAndData(craftBlock.position, craftBlockData.state, 1042);
        generatorAccess.minecraftWorld.notify(craftBlock.position, old, craftBlockData.state, 3)
    }

    private fun createBlocks(block: Block) {
        block.setTypeFast(Material.COBBLESTONE)
        block.getRelative(BlockFace.EAST).setTypeFast(Material.COBBLESTONE)
        block.getRelative(BlockFace.WEST).setTypeFast(Material.COBBLESTONE)
        block.getRelative(BlockFace.SOUTH).setTypeFast(Material.COBBLESTONE)
        block.getRelative(BlockFace.SOUTH_EAST).setTypeFast(Material.COBBLESTONE)
        block.getRelative(BlockFace.SOUTH_WEST).setTypeFast(Material.COBBLESTONE)
        block.getRelative(BlockFace.NORTH).setTypeFast(Material.COBBLESTONE)
        block.getRelative(BlockFace.NORTH_EAST).setTypeFast(Material.COBBLESTONE)
        block.getRelative(BlockFace.NORTH_WEST).setTypeFast(Material.COBBLESTONE)
    }

    @EventHandler
    fun playerFireEvent(e: EntityDamageEvent) {
        if (e.cause != EntityDamageEvent.DamageCause.FIRE && e.cause != EntityDamageEvent.DamageCause.FIRE_TICK && e.cause != EntityDamageEvent.DamageCause.LAVA)
            return
        if (e.entity !is Player)
            return
        val player = e.entity as Player
        val equipment = player.equipment ?: return
        if (allMagmaSet(equipment.armorContents)) {
            e.damage = 0.0
            player.fireTicks = 0
            e.isCancelled = true
        }

    }

    private fun allMagmaSet(armorContents: Array<ItemStack>): Boolean {
        for (item in armorContents)
            if (!hasLaveWalker(item))
                return false
        return true
    }

    private fun hasLaveWalker(item: ItemStack?): Boolean {
        return hasLaveWalker(item?.itemMeta)
    }

    private fun hasLaveWalker(meta: ItemMeta?): Boolean =
         meta?.hasPersistentData(BukkitConstants.LAVA_WALKER_ENCHANT)==true


    @EventHandler
    private fun playerMoveEvent(e: PlayerMoveEvent) {
        val itemStack = e.player.inventory.boots ?: return
        val itemMeta = itemStack.itemMeta ?: return
        if (!hasLaveWalker(itemMeta)) return
        val onToBlock = e.to?.block?.getRelative(BlockFace.DOWN) ?: return
//        if (allMagmaSet(e.player.equipment?.armorContents ?: return))
            if (onToBlock.type == Material.LAVA)
                createBlocks(onToBlock)

    }

}
