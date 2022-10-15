package com.astrainteractive.empire_items.empire_items.util

import ru.astrainteractive.astralibs.utils.HEX
import ru.astrainteractive.astralibs.utils.getHEXString
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.file_manager.FileManager


class Translations {
    companion object {
        lateinit var instance: Translations
        fun String.sendTo(player:Player) = player.sendMessage(this)
        @JvmName("argumentMessageExt")
        fun String.argumentMessage(vararg args: Pair<String, Any>) = argumentMessage(this, *args)
        fun argumentMessage(message: String, vararg args: Pair<String, Any>): String {
            var message = message
            args.forEach {
                message = message.replace(it.first, it.second.toString())
            }
            return message
        }
    }

    init {
        instance = this
    }

    private val _translationFile: FileManager = FileManager("translations.yml")
    private val translation = _translationFile.fileConfiguration
    private fun getHEXString(path: String, default: String): String {
        if (!translation.contains(path)) {
            translation.set(path, default)
            _translationFile.save()
        }
        return translation.getHEXString(path) ?: default.HEX()
    }



    //General
    val generalPrefix: String = "#18dbd1[EmpireItems]".HEX()

    val reload: String = getHEXString("general.reload", "#dbbb18Перезагрузка плагина")
    val reloadComplete: String =
        getHEXString("general.reload_complete", "#42f596Перезагрузка успешно завершена")
    val noPerms: String = getHEXString("general.no_permission", "#db2c18Недостаточно прав!")
    val notPlayer: String = getHEXString("general.not_a_player", "#db2c18ы не игрок!")
    val playerNotFound: String = getHEXString("general.player_not_found", "#db2c18Игрок не найден")
    val wrongArgs: String = getHEXString("general.wrong_args", "#db2c18Неверные аргументы")

    val itemNotExist: String = getHEXString("general.item_not_exist", "#db2c18Такого предмета нет")
    val itemGained: String = getHEXString("general.item_gained", "#18dbd1Вы получили %item%")
    val itemGave: String = getHEXString("general.item_gave", "#18dbd1Вы выдали %item% игроку %player%")
    val itemReplaced: String = getHEXString("general.item_replaced", "#18db52Предмет заменён")
    val diceThrow: String =
        getHEXString("general.dice_throw", "#18db52Игрок &6%player% #18db52бросил кубик. Значение &6%value%")

    //Zipping
    val zipStarted: String = getHEXString(
        "zipping.started",
        "Начинается архивирование.\nВремя зависит от количества файлов.\nДождитесь окончания архивирования."
    )
    val zipSuccess: String = getHEXString("zipping.success", "#42f596Архивирование успешно завершено")
    val zipError: String = getHEXString("zipping.error", "#f55442Ошибка при упакове ресурс-пака")


    //Upgrades
    val itemUpgradeAmountColor: String = getHEXString("upgrades.amount_color", "#777777")
    val itemUpgradeMaxHealth = getHEXString("upgrades.max_health", "Здоровье")
    val itemUpgradeKnockbackResistance = getHEXString("upgrades.knockback_resistance", "Сопротивление откидыванию")
    val itemUpgradeAttackDamage = getHEXString("upgrades.attack_damage", "Урон")
    val itemUpgradeAttackKnockback = getHEXString("upgrades.attack_knockback", "Откидывание")
    val itemUpgradeAttackSpeed = getHEXString("upgrades.attack_speed", "Скорость Атаки")
    val itemUpgradeArmor = getHEXString("upgrades.armor", "Броня")
    val itemUpgradeArmorToughness = getHEXString("upgrades.armor_toughness", "Прочность брони")
    val itemUpgradeMovementSpeed = getHEXString("upgrades.movement_speed", "Скорость")


    //EmGui
    val guiInfoDrop: String = getHEXString("gui.info_drop", "Выпадает из")
    val guiInfoDropColor: String = getHEXString("gui.info_drop_color", "#47ad5f")


    //Resource Pack
    val resourcePackDeny: String =
        getHEXString("resouce_pack.deny", "#f55442Вы отклонили ресурс-пак")
    val resourcePackMessage: String =
        "[\"\",{\"text\":\"-----------------------------------------------------------------------\",\"color\":\"#9C0EDC\"},{\"text\":\"\\n\"},{\"text\":\"Приветствуем на EmpireSMP!\",\"color\":\"#E4CC15\"},{\"text\":\"\\n\"},{\"text\":\"У нас куча новых предметов, мобов, новые меню и эмодзи!\",\"color\":\"#2ACADF\"},{\"text\":\"\\n\"},{\"text\":\"Всё это имеет новые текстурки и модельки!\",\"color\":\"#13C93A\"},{\"text\":\"\\n\"},{\"text\":\"Играя здесь у вас возникнет ощущение игры с модами!\",\"color\":\"#62C21D\"},{\"text\":\"\\n\"},{\"text\":\"Множество новых механик:\",\"color\":\"#AF8F08\"},{\"text\":\"\\n\"},{\"text\":\"Система рангов, апгрейды, оружие, ранги, новая руда, магазины\",\"color\":\"#C7BF1B\"},{\"text\":\"\\n\"},{\"text\":\"Не забудьте зайти в дискорд, если будут какие-то вопросы! /motd\",\"color\":\"#1784D3\"},{\"text\":\"\\n\"},{\"text\":\"EmpireProjekt.ru\",\"color\":\"#CE1881\"},{\"text\":\"\\n\"},{\"text\":\"------------------------------------------------------------------------\",\"color\":\"#7000ED\"}]".HEX()
    val resourcePackDownloadHint: String = getHEXString(
        "resource_pack.hint.download_self",
        "#f54542Попробуйте скачать самостоятельно. #429ef5empireprojekt.ru/files/EmpireProjektPack.zip"
    )
    val resourcePackHintTitle: String =
        getHEXString("resource_pack.hint.title", "#db2518Скачайте ресурс-пак")
    val resourcePackHintSubtitle: String = getHEXString(
        "resource_pack.hint.subtitle",
        "#18db4fВведите #189adb/empack. #18db4fОн нужен для новых предметов, звуков, оружие, брони и т.д"
    )
    val resourcePackDownloadError: String = getHEXString(
        "resource_pack.hint.error", """
        #f54542Не удалось загрузить ресурс-пак
        #f54542Скачайте его самостоятельно с #429ef5empireprojekt.ru/files/EmpireProjektPack.zip
        #f54542Дискорд https://discord.gg/Gwukdr8
        #f54542Группа ВК https://vk.com/EmpireProjekt
    """.trimIndent()
    )

    //Mobs
    val mobNotExist: String = getHEXString("mobs.not_exist", "#f5a742Такого моба нет")
    val mobFailedToSpawn: String = getHEXString("mobs.failed_to_spawn", "#f5a742Не удалось заспавнить моба")


    //Credits
    val cantHaveCredit: String = getHEXString("credit.cant_have", "#f5a742Вы не можете получить кредит.")
    val minForCredit: String = getHEXString("credit.min", "#f5a742Минимальный баланс для кредита %amount%.")
    val gaveCredit: String = getHEXString("credit.gained", "#f5a742Вы получили кредит: %amount%$.")
    val paidCredit: String = getHEXString("credit.paid", "#f5a742Вы выплатили кредит %amount%$.")
    val noActiveCredit: String = getHEXString("credit.no_active", "#f5a742У вас нет активных кредитов.")
    val notEnoughMoney: String = getHEXString("credit.no_money", "#f5a742У вас недостаточно денег.")

    //Bank
    val bankDeposit: String = getHEXString("credit.bank.deposit", "#f5a742Вы внесли в банк %amount%$.")
    val bankWithdraw: String = getHEXString("credit.bank.withdraw", "#f5a742Вы взяли из банка %amount%$.")


}