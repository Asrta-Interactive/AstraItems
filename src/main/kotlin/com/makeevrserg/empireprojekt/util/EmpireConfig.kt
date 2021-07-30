package com.makeevrserg.empireprojekt.util

import com.google.gson.annotations.SerializedName
import com.makeevrserg.empireprojekt.EmpirePlugin
import org.bukkit.configuration.file.FileConfiguration

data class EmpireConfig(
    @SerializedName("resourcePack")
    var resourcePackRef: String="",
    @SerializedName("downloadResourcePackOnJoin")
    var downloadPackOnJoin: Boolean = false,
    @SerializedName("tab_prefix")
    var tabPrefix: String="",
    @SerializedName("upgradeEnabled")
    private var isUpgradeEnabled: Boolean = true,
    @SerializedName("vampirismMultiplier")
    var vampirismMultiplier: Double = 0.05,
    @SerializedName("upgradeCostMultiplier")
    private var upgradeCostMultiplier: Double = 0.05,
    @SerializedName("onJoinResourcePackTimeStay")
    private var onJoinResourcePackTimeStay: Int = 200,
    @SerializedName("generate_blocks")
    public var generateBlocks: Boolean = false,
    @SerializedName("generate_only_on_new_chunks")
    public var generateOnlyOnNewChunks: Boolean = true,
    @SerializedName("debug_generating")
    public var generatingDebug: Boolean = false,
    @SerializedName("item_upgrade_break_multiplier")
    public var itemUpgradeBreakMultiplier: Int = 5
) {
    companion object {
        public fun create(): EmpireConfig {
            return EmpireYamlParser.parseYamlConfig(
                EmpirePlugin.empireFiles.configFile.getConfig(),
                EmpireConfig::class.java
            )?:EmpireConfig()
        }
    }
}