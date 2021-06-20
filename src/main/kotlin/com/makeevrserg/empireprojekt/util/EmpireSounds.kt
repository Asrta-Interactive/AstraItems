package com.makeevrserg.empireprojekt.util

import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.util.files.FileManager
import java.io.File

class EmpireSounds {
    val plugin: EmpirePlugin = EmpirePlugin.plugin
    private val _soundsFile: FileManager =
        FileManager(
            plugin,
            "config" + File.separator + "sounds.yml"
        )

    fun getSounds(): Map<String, List<String>> {
        val soundsFileConfig = _soundsFile.getConfig()?.getConfigurationSection("sounds")
        soundsFileConfig ?: return mutableMapOf()
        val map: MutableMap<String, List<String>> = mutableMapOf()

        for (key in soundsFileConfig.getKeys(false)) {
            val soundConfig = soundsFileConfig.getConfigurationSection(key)!!
            val list = soundConfig.getStringList("sounds")
            if (list.size == 0)
                continue
            map[soundConfig.getString("id") ?: continue] = soundConfig.getStringList("sounds")
        }
        return map
    }
    fun getNamespace(): String {
        return _soundsFile.getConfig()?.getString("namespace")?:return "empire_items"
    }
}