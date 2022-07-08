package block_generation

import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.inventory.meta.ItemMetaMock
import com.astrainteractive.astralibs.AstraEstimator
import com.astrainteractive.empire_items.EmpirePlugin
import com.astrainteractive.empire_items.api.utils.*
import com.astrainteractive.empire_items.empire_items.events.blocks.BlockGenerationEventUtils
import com.astrainteractive.empire_items.empire_items.events.blocks.BlockGenerationEventUtils.getBlocksLocations
import com.astrainteractive.empire_items.empire_items.util.TriplePair
import io.kotest.common.runBlocking
import junit.framework.TestCase.assertEquals
import org.bukkit.Chunk
import org.bukkit.ChunkSnapshot
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.block.BlockState
import org.bukkit.block.data.BlockData
import org.bukkit.entity.Entity
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.Plugin
import org.junit.Test
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import java.util.function.Predicate
import kotlin.random.Random

class BlockParserTest {
    @BeforeAll
    fun setupMockServer() {
    }

    @Test
    fun oneForEach() {
        val time = AstraEstimator.invoke {
            val xZShuffled = (0 until 15).shuffled() zip (0 until 15).shuffled()
            (0 until 2064).shuffled().flatMap { y ->
                xZShuffled.map { (x, z) -> TriplePair(x, y, z) }.map { it.first+it.second+it.third }
            }
        }
        println("Time passed: ${time/1000.0}")
    }
    @Test
    fun TwoForEach() {
        val time = AstraEstimator.invoke {
            val xZShuffled = (0 until 15).shuffled() zip (0 until 15).shuffled()
            (0 until 2064).shuffled().flatMap { y ->
                xZShuffled.map { it.first+it.second+y }
            }
        }
        println("Time passed: ${time/1000.0}")
    }
    @Test
    fun ThreeForEach() {
        val time = AstraEstimator.invoke {
            (0 until 15).shuffled().flatMap {x->
                (0 until 15).shuffled().flatMap {z->
                    (0 until 8192).shuffled().map { y->
                        x+y+z
                    }
                }
            }
        }
        println("Time passed: ${time/1000.0}")
    }

    @AfterAll
    fun onDisable() {
    }
}

