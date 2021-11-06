package com.astrainteractive.empireprojekt.credit

import com.earth2me.essentials.User
import com.astrainteractive.empireprojekt.EmpirePlugin
import com.astrainteractive.empireprojekt.credit.data.CreditPlayer
import org.bukkit.entity.Player
import java.math.BigDecimal

object CreditAPI {


    /**
     * Получение кредита у игрока. Если кредита нет - возвращаем 0
     */
    fun getCreditAmount(player: Player?): Int {
        return CreditPlayer.getPlayer(player)?.credit ?: 0
    }

    /**
     * Получение игрока как Essentials
     */
    fun getEssentialsPlayer(player: Player): User? {
        return EmpireCredit.essentials.getUser(player)
    }

    /**
     * Получение текущего баланса игрока
     */
    fun getPlayerBalance(player: Player): BigDecimal? {
        return getEssentialsPlayer(player)?.money
    }

    /**
     * Проверка на наличие кредита у игрока
     */
    fun hasCredit(player: Player): Boolean {
        val creditPlayer = CreditPlayer.getPlayer(player.uniqueId.toString())
            ?: return false
        if (creditPlayer.credit > 0)
            return true
        return false
    }

    /**
     * Сохранение информации об игроке
     */
    fun savePlayerConfig(creditPlayer: CreditPlayer) {
        val fileConfig = EmpireCredit.configFile.getConfig()
        fileConfig.set("players.${creditPlayer.uuid}.bank", creditPlayer.bank)
        fileConfig.set("players.${creditPlayer.uuid}.name", creditPlayer.name)
        fileConfig.set("players.${creditPlayer.uuid}.credit", creditPlayer.credit)
        fileConfig.set("players.${creditPlayer.uuid}.unix", creditPlayer.unix)
        fileConfig.set("players.${creditPlayer.uuid}.uuid", creditPlayer.uuid)
        EmpireCredit.configFile.saveConfig()
    }

    /**
     * Сохранение информации о кредите игрока
     */
    private fun saveCreditInfo(player: Player, amount: Int, updateTime: Boolean = false) {
        //Берем класс или создаем новый объект
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

    /**
     * Может ли игрок взять новый кредит
     */
    private fun canHaveCredit(player: Player, amount: BigDecimal): Boolean {
        //Имеет ли уже кредит
        if (hasCredit(player))
            return false
        //Проверяем, больше ли баланс минимума для кредита
        val balance = getPlayerBalance(player) ?: return false
        if (balance < EmpireCredit.config.minAmountForCredit.toBigDecimal())
            return false
        //Проверяем меньше ли баланс чем желаемый кредит
        if (balance < amount)
            return false
        return true

    }

    /**
     * Выдача кредита игроку
     */
    fun giveCredit(player: Player, amountStr: String) {
        //Желаемое количество
        val amount = amountStr.toBigDecimalOrNull() ?: return
        //Проверяем, может ли иметь кредит
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
        //Даем деньги и сохраняем конфиг
        getEssentialsPlayer(player)!!.giveMoney(amount)
        saveCreditInfo(player, amount.toInt(), true)
        player.sendMessage(EmpirePlugin.translations.GAVE_CREDIT.replace("%amount%", amountStr))

    }

    /**
     * Выплата кредита игроком
     */
    fun repayCredit(player: Player, amount: Int = getCreditAmount(player)) {
        //Если нет кредита - возвращаемся
        if (!hasCredit(player)) {
            player.sendMessage(EmpirePlugin.translations.NO_ACTIVE_CREDIT)
            return
        }

        //Берем кредитный класс игрока
        val creditPlayer = CreditPlayer.getPlayer(player) ?: return
        //Ссылка на Essentials
        val essPlayer = getEssentialsPlayer(player) ?: return

        //Проверяем хватает ли у игрока денег
        if (essPlayer.money.toInt() < amount) {
            player.sendMessage(EmpirePlugin.translations.NOT_ENOUGH_MONEY)
            return
        }
        //Выдаем кредит и сохраняем конфиги
        essPlayer.takeMoney(amount.toBigDecimal())
        saveCreditInfo(player, (creditPlayer.credit - amount / EmpireCredit.config.creditTax).toInt())
        player.sendMessage(
            EmpirePlugin.translations.PAID_CREDIT.replace(
                "%amount%",
                (amount).toString()
            )
        )

    }

}
