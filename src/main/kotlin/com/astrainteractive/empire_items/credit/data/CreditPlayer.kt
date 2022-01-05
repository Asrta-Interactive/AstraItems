package com.astrainteractive.empire_items.credit.data

import com.astrainteractive.astralibs.AstraYamlParser
import com.astrainteractive.empire_items.credit.EmpireCredit
import org.bukkit.entity.Player


data class CreditPlayer(
    val uuid: String,
    val name: String,
    var bank: Int = 0,
    var credit: Int = 0,
    var unix: Long = System.currentTimeMillis()
) {
    companion object {
        fun getPlayer(uuid: String): CreditPlayer? = AstraYamlParser.parser.configurationSectionToClass<CreditPlayer>(
            EmpireCredit.configFile.getConfig().getConfigurationSection("players.$uuid")
        )

        fun getPlayer(player: Player?): CreditPlayer? =
            getPlayer(player?.uniqueId.toString())

    }
}