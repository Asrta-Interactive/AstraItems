package com.makeevrserg.empireprojekt.betternpcs.data


import com.google.gson.annotations.Expose
import com.makeevrserg.empireprojekt.betternpcs.BetterNPCManager
import com.makeevrserg.empireprojekt.empirelibs.EmpireUtils
import com.makeevrserg.empireprojekt.empirelibs.EmpireYamlParser
import com.makeevrserg.empireprojekt.empirelibs.getHEXStringList
import net.minecraft.server.level.EntityPlayer
import org.bukkit.Location
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.ArmorStand

data class EmpireNPC(
    val id: String,
    val name: String? = null,
    val lines: List<String>? = null,
    val phrases: List<String>? = null,
    val commands: List<CommandEvent>? = null,
    val skin: Skin? = null,
    val location: Location
) {


    companion object {
        fun new(npc:String): EmpireNPC? {
            val section = BetterNPCManager.fileManager.getConfig().getConfigurationSection("npcs.$npc")?:return  null
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


    }
}