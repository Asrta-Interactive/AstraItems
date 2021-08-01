package empirelibs

import com.makeevrserg.empireprojekt.EmpirePlugin
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.yaml.snakeyaml.emitter.Emitable
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.lang.IllegalArgumentException

public class FileManager(var configName:String){

    private var configFiles: File? = null
    private var dataConfig: FileConfiguration? = null

    init {
        saveDefaultConfig()
    }


    private fun reloadConfig() {
        if (this.configFiles == null) this.configFiles = File(EmpirePlugin.instance.dataFolder, configName)
        dataConfig = YamlConfiguration.loadConfiguration(configFiles!!)
        val defaultStream = EmpirePlugin.instance.getResource(configName)
        if (defaultStream != null) {
            val defaultConfig = YamlConfiguration.loadConfiguration(InputStreamReader(defaultStream))
            this.dataConfig?.setDefaults(defaultConfig)
        }
    }

    fun getName(): String {
        return configName
    }


    fun getConfig(): FileConfiguration? {
        if (this.dataConfig == null) reloadConfig()
        return this.dataConfig
    }
    fun getFile(): File? {
        if (this.dataConfig == null) reloadConfig()
        return this.configFiles
    }

    fun LoadFiles() {
        configFiles = File(EmpirePlugin.instance.dataFolder, configName)
    }

    fun updateConfig(conf: FileConfiguration) {
        this.dataConfig = conf
    }

    fun saveConfig() {
        if (this.configFiles == null || this.dataConfig == null) return
        try {
            getConfig()?.save(this.configFiles!!)
        } catch (e: IOException) {
            println("${EmpirePlugin.translations.SAVE_ERROR} $configName")
        }
    }

    private fun saveDefaultConfig() {
        if (this.configFiles == null) this.configFiles = File(EmpirePlugin.instance.dataFolder, configName)
        try {
            if (!this.configFiles!!.exists()) EmpirePlugin.instance.saveResource(configName, false)
        }catch (e:IllegalArgumentException){
            println(EmpirePlugin.translations.NONSTANDART_FILE)
        }
    }
}