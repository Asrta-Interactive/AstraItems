package com.astrainteractive.empireprojekt.empire_items.util


import com.astrainteractive.astralibs.AstraYamlParser
import com.google.gson.annotations.SerializedName
import com.astrainteractive.empireprojekt.EmpirePlugin


data class EmpireConfig(
    @SerializedName(value = "resource_pack")
    var resourcePackRef: String?,
    @SerializedName(value = "download_resource_pack_on_join")
    var downloadPackOnJoin: Boolean,
    @SerializedName(value = "tab_prefix")
    var tabPrefix: String,
    @SerializedName(value = "is_upgrade_enabled")
    private var isUpgradeEnabled: Boolean,
    @SerializedName(value = "vampirism_multiplier")
    var vampirismMultiplier: Double,
    @SerializedName(value = "upgrade_cost_multiplier")
    private var upgradeCostMultiplier: Double,
    @SerializedName(value = "on_join_resource_pack_time_stay")
    private var onJoinResourcePackTimeStay: Int,
    @SerializedName(value = "generate_blocks")
    public var generateBlocks: Boolean,
    @SerializedName(value = "generate_only_on_new_chunks")
    public var generateOnlyOnNewChunks: Boolean,
    @SerializedName(value = "debug_generating")
    public var generatingDebug: Boolean,
    @SerializedName(value = "item_upgrade_break_multiplier")
    public var itemUpgradeBreakMultiplier: Int
) {


    companion object {
        fun new(): EmpireConfig {
            val section = EmpirePlugin.empireFiles.configFile.getConfig()
            val config = AstraYamlParser.fromYAML<EmpireConfig>(section,EmpireConfig::class.java)!!
            return config
        }

    }
}


