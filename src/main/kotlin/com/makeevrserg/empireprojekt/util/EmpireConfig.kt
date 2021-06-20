package com.makeevrserg.empireprojekt.util

import org.bukkit.configuration.file.FileConfiguration

class EmpireConfig(fileConfig: FileConfiguration?) {
    lateinit var resourcePackRef: String
    var downloadPackOnJoin: Boolean = false
    lateinit var tabPrefix: String
    private var isUpgradeEnabled: Boolean = true
    var vampirismMultiplier: Double = 0.05
    private var upgradeCostMultiplier: Double = 0.05
    private var onJoinResourcePackTimeStay:Int = 200
    private fun initConfig(fileConfig: FileConfiguration?) {
        fileConfig ?: return
        resourcePackRef = fileConfig.getString("resourcePack") ?: ""
        downloadPackOnJoin = fileConfig.getBoolean("downloadResourcePackOnJoin", false)
        tabPrefix = fileConfig.getString("tab_prefix", "") ?: ""
        isUpgradeEnabled = fileConfig.getBoolean("isUpgradeEnabled", true)
        vampirismMultiplier = fileConfig.getDouble("vampirismMultiplier", 0.05)
        upgradeCostMultiplier = fileConfig.getDouble("upgradeCostMultiplier", 1.0)
        onJoinResourcePackTimeStay = fileConfig.getInt("onJoinResourcePackTimeStay", 200)


    }

    init {
        initConfig(fileConfig)

    }


}