package npcs.data


import com.makeevrserg.empireprojekt.items.data.interact.CommandEvent
import empirelibs.getHEXStringList
import org.bukkit.Location
import org.bukkit.configuration.ConfigurationSection

data class EmpireNPC(
    val id: String,
    val lines: List<String>,
    val phrases: List<String>,
    val commands: List<CommandEvent>,
    val texture: String?,
    val signature: String?,
    val location: Location?
) {
    companion object {
        fun new(section: ConfigurationSection): EmpireNPC {
            val id = section.getString("id") ?: section.name
            val lines = section.getHEXStringList("lines")
            val phrases = section.getStringList("phrases")
            val commands = mutableListOf<CommandEvent>()//CommandEvent.newList(section.getConfigurationSection("commands"))
            val texture = section.getString("texture")
            val signature = section.getString("signature")
            val location = section.getLocation("location")
            return EmpireNPC(
                id = id,
                lines = lines,
                phrases = phrases,
                commands = commands,
                texture = texture,
                signature = signature,
                location = location
            )
        }
    }
}