package com.astrainteractive.empire_items

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.minecraft.world.level.GeneratorAccess
import net.minecraft.world.level.block.state.IBlockData
import net.minecraft.world.level.block.state.properties.BlockStateBoolean
import net.minecraft.world.level.block.state.properties.IBlockState
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.MultipleFacing
import org.bukkit.craftbukkit.v1_19_R1.block.CraftBlock
import org.bukkit.craftbukkit.v1_19_R1.block.data.CraftBlockData
import org.bukkit.craftbukkit.v1_19_R1.block.impl.CraftHugeMushroom
import ru.astrainteractive.astralibs.async.BukkitMain
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.utils.ReflectionUtil

object V1_19_2_FastBlockPlacer : IFastBlockPlacer {
    override suspend fun setTypeFast(type: Material, facing: Map<String, Boolean>, blockData: Int?, vararg blocks: Block) {
        val newData = type.createBlockData().apply {
            val craftBlockData = this as CraftBlockData
            val craftHugeMushroom = craftBlockData as MultipleFacing as CraftHugeMushroom
            val FACES: Array<BlockStateBoolean> = ReflectionUtil.getDeclaredField(craftHugeMushroom::class.java, "FACES")!!
            ReflectionUtil.setDeclaredField(
                craftHugeMushroom.javaClass.superclass,
                craftHugeMushroom,
                "parsedStates",
                null as Map<IBlockState<*>, Comparable<*>>?
            )
            for (f in facing) {
                val face = BlockFace.valueOf(f.key.uppercase())
                val state = FACES[face.ordinal]
                val newState = (craftHugeMushroom.state as IBlockData).a(state, f.value)
                ReflectionUtil.setDeclaredField(
                    craftHugeMushroom.javaClass.superclass,
                    craftHugeMushroom,
                    "state",
                    newState
                )
            }
        }


        withContext(Dispatchers.BukkitMain) {
            blocks.forEach { block ->
                val world = ((block as CraftBlock).craftWorld.handle as GeneratorAccess)
                val position = (block as CraftBlock).position
                val old = world.a_(position);
                val blockData: IBlockData = (newData as CraftBlockData).state
                world.a(position, blockData, 1042);
                world.minecraftWorld.a(position, old, blockData, 3)
            }
        }
    }

}