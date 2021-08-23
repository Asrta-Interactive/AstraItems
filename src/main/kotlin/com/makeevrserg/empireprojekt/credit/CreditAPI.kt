package com.makeevrserg.empireprojekt.credit

import com.earth2me.essentials.User
import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.credit.data.CreditPlayer
import org.bukkit.entity.Player
import java.math.BigDecimal

class CreditAPI {
    companion object {

        fun getCreditAmount(player: Player?): Int {
            return CreditPlayer.getPlayer(player)?.credit ?: 0
        }

        fun getEssentialsPlayer(player: Player): User? {
            return EmpireCredit.essentials.getUser(player)
        }

        fun getPlayerBalance(player: Player): BigDecimal? {
            return getEssentialsPlayer(player)?.money
        }

        fun hasCredit(player: Player): Boolean {
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

        private fun saveCreditInfo(player: Player, amount: Int,updateTime:Boolean=false) {
            val creditPlayer = CreditPlayer.getPlayer(player) ?: CreditPlayer(
                uuid = player.uniqueId.toString(),
                name = player.name,
                credit = amount
            )
            if (updateTime)
                creditPlayer.unix = System.currentTimeMillis()
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
                player.sendMessage(
                    EmpirePlugin.translations.MIN_FOR_CREDIT.replace(
                        "%amount%",
                        EmpireCredit.config.minAmountForCredit.toString()
                    )
                )
                return
            }
            getEssentialsPlayer(player)!!.giveMoney(amount)
            saveCreditInfo(player, amount.toInt(),true)
            player.sendMessage(EmpirePlugin.translations.GAVE_CREDIT.replace("%amount%", amountStr))

        }

        public fun getPercentOfCredit(player: Player): Double? {
            val creditPlayer = CreditPlayer.getPlayer(player) ?: return null

            var amountToPay = creditPlayer.credit * EmpireCredit.config.creditTax
            if (amountToPay>200)
                amountToPay = EmpireCredit.config.creditWithdrawTax
                    ?: 25.0 / 100 * creditPlayer.credit
            val essPlayer = getEssentialsPlayer(player)?:return null
            if (BankAPI.getBankAmount(player)>amountToPay)
                BankAPI.withdrawMoney(player,amountToPay.toInt().toString())

            if (amountToPay>essPlayer.money.toInt())
                amountToPay = essPlayer.money.toDouble()
            if (amountToPay<1)
                return null
            return amountToPay
        }

        public fun repayCredit(player: Player, amount: Int? = null) {
            if (!hasCredit(player)) {
                player.sendMessage(EmpirePlugin.translations.NO_ACTIVE_CREDIT)
                return
            }
            val creditPlayer = CreditPlayer.getPlayer(player) ?: return
            val essPlayer = getEssentialsPlayer(player) ?: return
            if (essPlayer.money.toInt() < amount?.toDouble() ?: (creditPlayer.credit * EmpireCredit.config.creditTax)) {
                player.sendMessage(EmpirePlugin.translations.NOT_ENOUGH_MONEY)
                return
            }


            essPlayer.takeMoney(
                amount?.toBigDecimal() ?: (creditPlayer.credit * EmpireCredit.config.creditTax).toBigDecimal()
            )

            if (amount != null)
                saveCreditInfo(player, (creditPlayer.credit - amount / EmpireCredit.config.creditTax).toInt())
            else
                saveCreditInfo(player, 0)

            val paid = if (amount == null) creditPlayer.credit * EmpireCredit.config.creditTax else amount
            player.sendMessage(
                EmpirePlugin.translations.PAID_CREDIT.replace(
                    "%amount%",
                    (paid).toString()
                )
            )

        }

    }
}