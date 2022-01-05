package com.astrainteractive.empireprojekt.credit

import com.astrainteractive.empireprojekt.EmpirePlugin
import com.astrainteractive.empireprojekt.credit.data.CreditPlayer
import org.bukkit.entity.Player

object BankAPI {


    /**
     * Получение банковского счета игрока
     */
    fun getBankAmount(player: Player?): Int {
        return CreditPlayer.getPlayer(player)?.bank ?: 0
    }

    /**
     * Внести деньги в банк
     */
    fun depositMoney(player: Player, amountStr: String? = null) {
        //Ссылка на Essentials
        val playerEss = CreditAPI.getEssentialsPlayer(player) ?: return
        //Количество желаемых денег на внесени
        val amount = amountStr?.toIntOrNull() ?: playerEss.money.toInt()
        //Получение кредитного класса. Если нет - создаем.
        val creditPlayer = CreditPlayer.getPlayer(player) ?: CreditPlayer(player.uniqueId.toString(), player.name)


        //Может ли игрок внести столько денег
        if (amount > playerEss.money.toInt()) {
            player.sendMessage(EmpirePlugin.translations.notEnoughMoney)
            return
        }
        //Забираем деньги у игрока и сохраняем конфиг
        playerEss.takeMoney(amount.toBigDecimal())
        creditPlayer.bank += amount
        CreditAPI.savePlayerConfig(creditPlayer)
        player.sendMessage(EmpirePlugin.translations.bankDeposit.replace("%amount%", amount.toString()))
    }

    /**
     * Вывести деньги из банка
     */
    fun withdrawMoney(player: Player, amountStr: String? = null) {
        //Получаем ссылку на кредитный класс. Если нет - значит и кредита нет
        val creditPlayer = CreditPlayer.getPlayer(player) ?: return

        //Получаем количество денег желаемое вывести
        val amount = amountStr?.toIntOrNull() ?: creditPlayer.bank
        //Ссылка на Essentials
        val playerEss = CreditAPI.getEssentialsPlayer(player) ?: return
        //Проверяем, есть ли такое количество в банке
        if (amount > creditPlayer.bank) {
            player.sendMessage(EmpirePlugin.translations.notEnoughMoney)
            return
        }
        //Даем игроку деньги и сохраняем конфиг
        playerEss.giveMoney(amount.toBigDecimal())
        creditPlayer.bank -= amount
        CreditAPI.savePlayerConfig(creditPlayer)
        player.sendMessage(EmpirePlugin.translations.bankWithdraw.replace("%amount%", amount.toString()))
    }

}