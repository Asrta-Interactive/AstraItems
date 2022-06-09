//package serialization
//
//import com.astrainteractive.empire_items.api.drop.AstraDrop
//import com.astrainteractive.empire_items.api.drop.CustomDropSection
//import com.astrainteractive.empire_items.models.ItemYamlFile
//import com.astrainteractive.empire_items.empire_items.util.EmpireSerializer
//import junit.framework.Assert.assertEquals
//import org.junit.Test
//import java.io.File
//
//object Files {
//    val full: File
//        get() = File("src/test/resources/serialization/full.yml")
//    val wrong_parsing: File
//        get() = File("src/test/resources/serialization/wrong_parsing.yml")
//    val fullDrop: File
//        get() = File("src/test/resources/serialization/drop/full.yml")
//    val empty1: File
//        get() = File("src/test/resources/serialization/drop/empty_1.yml")
//    val empty2: File
//        get() = File("src/test/resources/serialization/drop/empty_2.yml")
//}
//
//class ClassesSerialization {
//    @Test
//    fun `Check drop withEverything`() {
//        val full = EmpireSerializer.toClass<CustomDropSection>(Files.fullDrop)
//        assert(full != null)
//        assertEquals(
//            full!!.loot!!["bottle_for_slime"],
//            AstraDrop(id = "bottle_for_slime", dropFrom = "ZOMBIE", minAmount = 1, maxAmount = 2, chance = 0.2)
//        )
//        val empty1 = EmpireSerializer.toClass<CustomDropSection>(Files.empty1)
//        assert(empty1 == null)
//        val empty2 = EmpireSerializer.toClass<CustomDropSection>(Files.empty2)
//        assert(empty2 == null)
//    }
//
//    @Test
//    fun checkSuperClass(){
//        val file = EmpireSerializer.toClass<ItemYamlFile>(Files.full)
//        assert(file!=null)
//        EmpireSerializer.toClass<ItemYamlFile>(Files.wrong_parsing)
//        println(file)
//    }
//}