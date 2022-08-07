package block_generation

import com.astrainteractive.astralibs.AstraEstimator
import com.astrainteractive.astralibs.utils.TriplePair
import com.astrainteractive.empire_items.api.utils.*
import org.junit.Test
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll

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

