package com.makeevrserg.empireprojekt.credit

import com.earth2me.essentials.User
import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.credit.data.CreditPlayer
import org.bukkit.entity.Player
import java.math.BigDecimal

class CreditAPI {
    companion object {

        fun getCreditAmount(player: Player?): Int {
            return CreditPlayer.getPlayer(player)?.credit?:0
        }

        fun getEssentialsPlayer(player: Player): User? {
            return EmpireCredit.essentials.getUser(player)
        }

        fun getPlayerBalance(player: Player): BigDecimal? {
            return getEssentialsPlayer(player)?.money
        }

        private fun hasCredit(player: Player): Boolean {
            val creditPlayer = CreditPlayer.getPlayer(player.uniqueId.toString())
                ?: return false
            if (creditPlayer.credit > 0)
                return true
            return false
        }

        public fun savePlayerConfig(creditPlayer: CreditPlayer) {
            val fileConfig = EmpireCredit.configFile.getConfig()
            fileConfig.set("players.${creditPlayer.uuid}.bank", creditPlayer.bank)
            fileConfig.set("players.${creditPlayer.uuid}.name", creditPlayer.name)
            fileConfig.set("players.${creditPlayer.uuid}.credit", creditPlayer.credit)
            fileConfig.set("players.${creditPlayer.uuid}.unix", creditPlayer.unix)
            fileConfig.set("players.${creditPlayer.uuid}.uuid", creditPlayer.uuid)
            EmpireCredit.configFile.saveConfig()
        }

        private fun saveCreditInfo(player: Player, amount: Int) {
            val creditPlayer = CreditPlayer.getPlayer(player) ?: CreditPlayer(
                uuid = player.uniqueId.toString(),
                name = player.name,
                credit = amount
            )
            creditPlayer.credit = amount
            savePlayerConfig(creditPlayer)
        }

        private fun canHaveCredit(player: Player, amount: BigDecimal): Boolean {
            if (hasCredit(player))
                return false
            val balance = getPlayerBalance(player) ?: return false
            if (balance < EmpireCredit.config.minAmountForCredit.toBigDecimal())
                return false
            return true

        }

        public fun giveCredit(player: Player, amountStr: String) {
            val amount = amountStr.toBigDecimalOrNull() ?: return
            if (!canHaveCredit(player, amount)) {
                player.sendMessage(EmpirePlugin.translations.CANT_HAVE_CREDIT)
                player.sendMessage(EmpirePlugin.translations.MIN_FOR_CREDIT.replace("%amount%",EmpireCredit.config.minAmountForCredit.toString()))
                return
            }
            getEssentialsPlayer(player)!!.giveMoney(amount)
            saveCreditInfo(player, amount.toInt())
            player.sendMessage(EmpirePlugin.translations.GAVE_CREDIT.replace("%amount%",amountStr))

        }

        public fun repayCredit(player: Player) {
            if (!hasCredit(player)) {
                player.sendMessage(EmpirePlugin.translations.NO_ACTIVE_CREDIT)
                return
            }
            val creditPlayer = CreditPlayer.getPlayer(player) ?: return
            val essPlayer = getEssentialsPlayer(player) ?: return
            if (essPlayer.money.toInt() < creditPlayer.credit * EmpireCredit.config.creditTax) {
                player.sendMessage(EmpirePlugin.translations.NOT_ENOUGH_MONEY)
                return
            }


            essPlayer.takeMoney((creditPlayer.credit * EmpireCredit.config.creditTax).toBigDecimal())
            saveCreditInfo(player, 0)
            player.sendMessage(EmpirePlugin.translations.PAID_CREDIT.replace("%amount%",(creditPlayer.credit*EmpireCredit.config.creditTax).toString()))

        }

    }
}