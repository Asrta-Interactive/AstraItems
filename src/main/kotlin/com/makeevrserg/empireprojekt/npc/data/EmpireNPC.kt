package com.makeevrserg.empireprojekt.npc.data


import com.makeevrserg.empireprojekt.npc.NPCManager
import com.makeevrserg.empireprojekt.empirelibs.EmpireUtils
import com.makeevrserg.empireprojekt.empirelibs.getHEXStringList
import org.bukkit.Location

data class EmpireNPC(
    val id: String,
    val name: String? = null,
    val lines: List<String>? = null,
    val phrases: List<String>? = null,
    val commands: List<CommandEvent>? = null,
    var skin: Skin? = null,
    var location: Location
) {


    companion object {
        fun new(npc:String): EmpireNPC? {
            val section = NPCManager.fileManager.getConfig().getConfigurationSection("npcs.$npc")?:return  null
            val id = npc
            val lines = section.getHEXStringList("lines")
            val name = EmpireUtils.HEXPattern(section.getString("name"))
            val phrases = section.getHEXStringList("phrases")
            val commands = CommandEvent.new(section.getConfigurationSection("commands"))
            val skin = Skin.new(section.getConfigurationSection("skin"))
            val location = section.getLocation("location")?:return null
            return EmpireNPC(
                id = id,
                name = name,
                lines = lines,
                phrases = phrases,
                commands = commands,
                skin = skin,
                location = location
            )
        }

        fun save(npc: EmpireNPC){
            val config = NPCManager.fileManager.getConfig()
            val path = "npcs.${npc.id}"
            config.set("$path.location",npc.location)
            config.set("$path.skin.value",npc.skin?.value)
            config.set("$path.skin.signature",npc.skin?.signature)
            NPCManager.fileManager.saveConfig()
        }


    }
}