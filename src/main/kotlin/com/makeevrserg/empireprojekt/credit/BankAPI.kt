package com.makeevrserg.empireprojekt.credit

import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.credit.data.CreditPlayer
import org.bukkit.entity.Player

class BankAPI {
    companion object{


        fun getBankAmount(player: Player?): Int {
            return CreditPlayer.getPlayer(player)?.bank?:0
        }

        fun depositMoney(player:Player,amountStr:String?=null){
            val playerEss = CreditAPI.getEssentialsPlayer(player)?:return
            val amount = amountStr?.toIntOrNull()?:playerEss.money.toInt()
            val creditPlayer = CreditPlayer.getPlayer(player)?: CreditPlayer(player.uniqueId.toString(),player.name)
            val balance = playerEss.money

            if (amount>balance.toInt()){
                player.sendMessage(EmpirePlugin.translations.NOT_ENOUGH_MONEY)
                return
            }
            playerEss.takeMoney(amount.toBigDecimal())
            creditPlayer.bank+=amount
            CreditAPI.savePlayerConfig(creditPlayer)
            player.sendMessage(EmpirePlugin.translations.BANK_DEPOSIT.replace("%amount%",amount.toString()))
        }
        fun withdrawMoney(player: Player,amountStr: String?=null){
            val creditPlayer = CreditPlayer.getPlayer(player)?:return
            val amount = amountStr?.toIntOrNull()?:creditPlayer.bank

            val playerEss = CreditAPI.getEssentialsPlayer(player)?:return
            if (amount>creditPlayer.bank){
                player.sendMessage(EmpirePlugin.translations.NOT_ENOUGH_MONEY)
                return
            }
            playerEss.giveMoney(amount.toBigDecimal())
            creditPlayer.bank-=amount
            CreditAPI.savePlayerConfig(creditPlayer)
            player.sendMessage(EmpirePlugin.translations.BANK_WITHDRAW.replace("%amount%",amount.toString()))
        }
    }
}