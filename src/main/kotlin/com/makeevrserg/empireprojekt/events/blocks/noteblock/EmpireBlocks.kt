//package com.makeevrserg.empireprojekt.events.blocks.noteblock
//
//import com.makeevrserg.empireprojekt.EmpirePlugin
//import com.makeevrserg.empireprojekt.util.EmpireUtils
//import net.minecraft.core.BlockPosition
//import net.minecraft.network.protocol.game.PacketPlayOutBlockBreakAnimation
//import org.bukkit.*
//import org.bukkit.block.Block
//import org.bukkit.block.BlockFace
//import org.bukkit.block.data.BlockData
//import org.bukkit.block.data.type.NoteBlock
//import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer
//import org.bukkit.entity.Player
//import org.bukkit.event.EventHandler
//import org.bukkit.event.Listener
//import org.bukkit.event.block.*
//import org.bukkit.event.player.PlayerAnimationEvent
//import org.bukkit.event.player.PlayerInteractEvent
//import org.bukkit.potion.PotionEffect
//import org.bukkit.potion.PotionEffectType
//
//
//class EmpireBlocks() : Listener {
//
//    data class NoteBlockVariant(
//        val instrument: Instrument,
//        val note: Int
//    )
//
//    private fun Block?.noteBlockData(): NoteBlock? {
//        this ?: return null
//        if (this.blockData !is NoteBlock)
//            return null
//        return blockData as NoteBlock
//    }
//
//    private fun playNote(block: Block) {
//        val blockUnder = block.getRelative(BlockFace.DOWN)
//        val material = blockUnder.type
//        block.location.world?.spawnParticle(Particle.NOTE, block.location.add(0.0, 1.0, 0.0), 1) ?: return
//    }
//
//    @EventHandler
//    public fun onNoteBlockPhysics(e: BlockPhysicsEvent) {
//        val noteBlock = e.block.noteBlockData()
//        val blockAbove = e.block.getRelative(BlockFace.UP)
//        val noteBlockAbove = blockAbove.noteBlockData()
//        if (e.block.type == Material.NOTE_BLOCK)
//            e.isCancelled = true
//        if (noteBlock!=null || noteBlockAbove!=null)
//            e.isCancelled = true
//        if (noteBlock != null)
//            e.block.state.update(true, false)
//        if (noteBlockAbove != null)
//            blockAbove.state.update(true, false)
//
//        println("${noteBlock?.instrument} ${noteBlockAbove?.instrument} ${e.isCancelled}")
//        if (noteBlock?.instrument == Instrument.PIANO)
//            playNote(e.block)
//
//
//    }
//
//    @EventHandler
//    fun blockRedstoneEvent(e: BlockRedstoneEvent) {
//        println(e.block)
//        val noteBlock = e.block.noteBlockData() ?: return
//    }
//
//    @EventHandler
//    public fun onNoteBlockInteract(e: PlayerInteractEvent) {
//        if (e.action != Action.RIGHT_CLICK_BLOCK)
//            return
//
//
//        val block = e.clickedBlock ?: return
//        val noteBlock = block.noteBlockData() ?: return
//        if (noteBlock.instrument != Instrument.PIANO) {
//            if (!e.player.isSneaking)
//                e.isCancelled = true
//            return
//        }
//    }
//
//    private fun getNoteBlockData(block: Block): BlockData {
//        if (block.blockData !is NoteBlock)
//            return block.blockData
//        val noteBlock = block.blockData as NoteBlock
//        noteBlock.instrument = Instrument.PIANO
//        return noteBlock
//    }
//
//    @EventHandler
//    public fun blockPlaceEvent(e: BlockPlaceEvent) {
//        val item = e.itemInHand
//        val id = EmpireUtils.getEmpireID(item) ?: return
//        val empireBlock = EmpirePlugin.empireItems._empireBlocks[id] ?: return
//
//        val block = e.block
//        block.type = Material.NOTE_BLOCK
//        val noteBlock = block.blockData as NoteBlock
//        noteBlock.instrument = NoteBlockAPI.getInstrumentByData(empireBlock.data)
//        noteBlock.note = Note(NoteBlockAPI.getNoteByData(empireBlock.data))
//        block.blockData = noteBlock
//        block.location.world!!.playSound(block.location, empireBlock.place_sound ?: "", 1.0f, 1.0f)
//    }
//
//    @EventHandler
//    public fun blockBreakEvent(e: BlockBreakEvent) {
//        val block = e.block
//        val noteBlock = block.noteBlockData() ?: return
//        if (noteBlock.instrument == Instrument.PIANO)
//            return
//
//        val data = NoteBlockAPI.getDataByNoteBlock(noteBlock)
//        val id = EmpirePlugin.empireItems._empireBlocksByData[data] ?: return
//        val item = EmpirePlugin.empireItems.empireItems[id] ?: return
//        val empireBlock = EmpirePlugin.empireItems._empireBlocks[id] ?: return
//        block.location.world!!.playSound(block.location, empireBlock.break_sound ?: "", 1.0f, 1.0f)
//        block.location.world!!.dropItem(block.location, item)
//        e.isDropItems = false
//
//    }
//
//
//    val blockDamageMap = mutableMapOf<Player, Long>()
//
//    @EventHandler
//    fun BlockDamageEvent(e: BlockDamageEvent) {
//        val noteblock = e.block.noteBlockData()
//        if (noteblock == null || noteblock.instrument == Instrument.PIANO) {
//            blockDamageMap.remove(e.player)
//            return
//        }
//        val data = NoteBlockAPI.getDataByNoteBlock(noteblock)
//        e.player.sendBlockBreakPacket(e.block, 10)
//        blockDamageMap[e.player] = System.currentTimeMillis()
//    }
//
//    @EventHandler
//    fun BlockBreakEvent(e: BlockBreakEvent) {
//        blockDamageMap.remove(e.player)
//        e.player.sendBlockBreakPacket(e.block, 10)
//    }
//
//    private fun Player.sendBlockBreakPacket(block: Block, breakProgress: Int) {
//        val packet = PacketPlayOutBlockBreakAnimation(
//            10,
//            BlockPosition(block.x, block.y, block.z),
//            breakProgress
//        )
//        (this as CraftPlayer).handle.b.sendPacket(packet)
//    }
//
//    @EventHandler
//    fun playerBreakingEvent(e: PlayerAnimationEvent) {
//        val block = e.player.getTargetBlock(null, 100)
//        val player = e.player
//        val noteBlock = block.noteBlockData() ?: return
//        val data = NoteBlockAPI.getDataByNoteBlock(noteBlock)
//        val id = EmpirePlugin.empireItems._empireBlocksByData[data] ?: return
//        val item = EmpirePlugin.empireItems.empireItems[id] ?: return
//        val empireBlock = EmpirePlugin.empireItems._empireBlocks[id] ?: return
//        val time = System.currentTimeMillis().minus(blockDamageMap[e.player] ?: return) / 10.0
//
//
//        player.sendBlockBreakPacket(block, (time / empireBlock.hardness.toDouble() * 9).toInt())
//
//
//        if (time > empireBlock.hardness) {
//            player.breakBlock(block)
//            player.sendBlockBreakPacket(block, 10)
//        }
//
//        player.addPotionEffect(
//            PotionEffect(PotionEffectType.SLOW_DIGGING, 5, 200, false, false, false)
//        )
//
//    }
//
//    @EventHandler
//    fun notePlayEvent(e: NotePlayEvent) {
//        println("note")
//    }
//
//
//    //var blockGeneration: BlockGeneration? = null
//
//    init {
//        EmpirePlugin.instance.server.pluginManager.registerEvents(this, EmpirePlugin.instance)
////        if (EmpirePlugin.config.generateBlocks)
////            blockGeneration = BlockGeneration()
//
//    }
//
//    public fun onDisable() {
//        BlockPlaceEvent.getHandlerList().unregister(this)
//        BlockPhysicsEvent.getHandlerList().unregister(this)
//        BlockBreakEvent.getHandlerList().unregister(this)
//        BlockDamageEvent.getHandlerList().unregister(this)
//        PlayerAnimationEvent.getHandlerList().unregister(this)
//        NotePlayEvent.getHandlerList().unregister(this)
//        BlockRedstoneEvent.getHandlerList().unregister(this)
//        Bukkit.getScheduler().cancelTasks(EmpirePlugin.instance)
////        if (blockGeneration != null)
////            blockGeneration!!.onDisable()
//
//    }
//}