package com.makeevrserg.empireprojekt.credit.data

import com.makeevrserg.empireprojekt.credit.EmpireCredit
import com.makeevrserg.empireprojekt.empirelibs.EmpireYamlParser
import org.bukkit.entity.Player


data class CreditPlayer(
    val uuid: String,
    val name: String,
    var bank:Int = 0,
    var credit: Int = 0,
    var unix: Long = System.currentTimeMillis()
) {
    companion object {
        fun getPlayer(uuid: String): CreditPlayer? =
            EmpireYamlParser.fromYAML<CreditPlayer>(
                EmpireCredit.configFile.getConfig(),
                CreditPlayer::class.java,
                listOf("players", uuid)
            )
        fun getPlayer(player: Player?): CreditPlayer? =
            getPlayer(player?.uniqueId.toString())

    }
}