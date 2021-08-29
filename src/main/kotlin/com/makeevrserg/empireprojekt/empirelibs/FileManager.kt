package com.makeevrserg.empireprojekt.empirelibs

import com.makeevrserg.empireprojekt.EmpirePlugin
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.lang.IllegalArgumentException

/**
 * File manager for every single file
 * You can create new files, change them, save/load them
 * If file not exist in resouces, it will be created anyway
 * @param configName is name of the file with file type
 */
public class FileManager(var configName:String){


    /**
     * Reference for the file
     */
    private var configFiles: File? = null
    /**
     * Reference for file configuration
     */
    private var dataConfig: FileConfiguration? = null

    /**
     * Initialization of file
     */
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


    fun getConfig(): FileConfiguration {
        if (this.dataConfig == null) reloadConfig()
        return this.dataConfig!!
    }
    fun getFile(): File {
        if (this.dataConfig == null) reloadConfig()
        return this.configFiles!!
    }

    fun LoadFiles() {
        configFiles = File(EmpirePlugin.instance.dataFolder, configName)
    }

    fun updateConfig(conf: FileConfiguration) {
        this.dataConfig = conf
    }


    /**
     * function allows you to save dataConfig
     */
    fun saveConfig() {
        if (this.configFiles == null || this.dataConfig == null) return
        try {
            getConfig().save(this.configFiles!!)
        } catch (e: IOException) {
            println("${EmpirePlugin.translations.SAVE_ERROR} $configName")
        }
    }

    /**
     * Initialization of file
     */
    private fun saveDefaultConfig() {
        if (this.configFiles == null) this.configFiles = File(EmpirePlugin.instance.dataFolder, configName)
        try {
            if (!this.configFiles!!.exists()) EmpirePlugin.instance.saveResource(configName, false)
        }catch (e:IllegalArgumentException){
            println(EmpirePlugin.translations.NONSTANDART_FILE)
        }
    }
}