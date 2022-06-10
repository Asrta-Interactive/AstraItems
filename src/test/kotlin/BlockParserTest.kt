import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.inventory.meta.ItemMetaMock
import com.astrainteractive.empire_items.EmpirePlugin
import com.astrainteractive.empire_items.api.utils.*
import io.kotest.common.runBlocking
import junit.framework.TestCase.assertEquals
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.junit.Test
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll

class BlockParserTest {
    var enabled:Boolean = false
    @BeforeAll
    fun setupMockServer() {
        runBlocking {
            enabled = false
            val server = MockBukkit.mock()
            val plugin = (MockBukkit.load(EmpirePlugin::class.java) as EmpirePlugin)
            Utility.sendTestFiles()
            plugin.reloadConfig()
            enabled = true
        }
    }
    fun initialize(){
        while (!enabled) continue
    }
    @Test
    fun `Cooldown check`(){
        initialize()
        val cooldown = Cooldown<String>()
        cooldown.setCooldown("test")
        assertEquals(cooldown.hasCooldown("text"), true)
        assertEquals(cooldown.hasCooldown("text", 1000L), true)
        assertEquals(cooldown.hasCooldown("text", -10L), false)
        assertEquals(cooldown.hasCooldown("text", 0L), false)
    }
    @Test
    fun `Check BukkitAstraData`(){
        initialize()
        val someConstant = BukkitConstant("someKey", PersistentDataType.INTEGER)
        val itemStack = ItemStack(Material.DIAMOND).apply { itemMeta = ItemMetaMock() }
        val itemMeta = itemStack.itemMeta!!
        assertEquals(itemMeta.getPersistentData(someConstant) == null, true)
        assertEquals(itemMeta.hasPersistentData(someConstant), false)
        itemMeta.setPersistentDataType(someConstant, 0)
        assertEquals(itemMeta.getPersistentData(someConstant), 0)
        assertEquals(itemMeta.hasPersistentData(someConstant), true)

    }

    @AfterAll
    fun onDisable() {
        MockBukkit.unmock()
    }
}