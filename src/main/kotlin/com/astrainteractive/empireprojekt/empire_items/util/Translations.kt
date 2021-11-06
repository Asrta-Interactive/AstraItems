package com.astrainteractive.empireprojekt.empire_items.util

import com.astrainteractive.astralibs.FileManager
import com.astrainteractive.astralibs.getHEXString
import com.astrainteractive.empireprojekt.EmpirePlugin
import org.bukkit.ChatColor
import java.io.File


class Translations {

    private val plugin = EmpirePlugin.instance
    val _translationFile: FileManager = FileManager("config" + File.separator + "translations.yml")
    private val translationFile = _translationFile.getConfig()!!

    val PLUGIN_PREFIX: String = translationFile.getHEXString("PLUGIN_PREFIX", "#18dbd1[EmpireItems]")

    //Plugin
    val RELOAD: String = translationFile.getHEXString("RELOAD", "#dbbb18Перезагрузка плагина")
    val RELOAD_COMPLETE: String =
        translationFile.getHEXString("RELOAD_COMPLETE", "#42f596Перезагрузка успешно завершена")

    //Files
    val SAVE_ERROR: String =
        translationFile.getHEXString("SAVE_ERROR", "#db2c18Не удалось сохранить файл")
    val NONSTANDART_FILE: String = translationFile.getHEXString("NONSTANDART_FILE", "#db2c18Нестнадартный файл")
    val LOADING_FILE: String =
        translationFile.getHEXString("LOADING_FILE", "#18dbd1Загрузка файла")
    val NOT_EXIST_FILE: String = translationFile.getHEXString("NOT_EXIST_FILE", "#db2c18Файл не существует")

    //Permissions
    val NO_PERMISSION: String = translationFile.getHEXString("NO_PERMISSION", "#db2c18Недостаточно прав!")

    //Arguments
    val WRONG_ARGS: String = translationFile.getHEXString("NO_PERMISSION", "#db2c18Неверные аргументы")
    val WRONG_NUMBER: String = translationFile.getHEXString("WRONG_NUMBER", "#db2c18Количество задано неверно")

    //Inventory Saver
    val INVENTORY_SAVED: String = translationFile.getHEXString("NO_PERMISSION", "#42f596Инвентарь сохранен")
    val INVENTORY_LOADED: String = translationFile.getHEXString("NO_PERMISSION", "#42f596Инвентарь загргужен")


    val SUCCESS: String = translationFile.getHEXString("SUCCESS", "#42f596Успех!")

    //Config load
    val PLUGIN_WRONG_SYNTAX_ITEM: String = translationFile.getHEXString(
        "PLUGIN_WRONG_SYNTAX_ITEM",
        "#db2c18Ошибка при загрузке файла. Вероятно, указаны неверные параметры предмета."
    )
    val EXISTED_CUSTOM_MODEL_DATA: String = translationFile.getHEXString(
        "EXISTED_CUSTOM_MODEL_DATA",
        "#db2c18Внимание! Введене уже существующая custom_model_data."
    )
    val CUSTOM_BLOCK_WRONG_VALUE: String = translationFile.getHEXString(
        "CUSTOM_BLOCK_WRONG_VALUE",
        "#db2c18Внимание! Введене неверная data в поле block! Значение data должно находится в отрезке [0,191]."
    )

    val FILE_WRONG_PARSE: String =
        translationFile.getHEXString("FILE_WRONG_PARSE", "#f55442 Ошибка при парсинге файла: ")

    //Sit
    val SIT_IN_AIR: String = translationFile.getHEXString("SIT_IN_AIR", "#dbbb18Вы в воздухе")
    val SIT_ALREADY: String = translationFile.getHEXString("SIT_ALREADY", "#dbbb18Вы уже сидите")

    //Items
    val ITEM_GAINED: String = translationFile.getHEXString("ITEM_GAINED", "#18dbd1Вы получили")
    val ITEM_GIVE: String = translationFile.getHEXString("ITEM_GIVE", "#18dbd1Вы выдали")
    val ITEM_NOT_FOUND: String = translationFile.getHEXString("ITEM_NOT_FOUND", "#db2c18Нет предмета")
    val WRONG_NAMESPACE: String =
        translationFile.getHEXString("WRONG_NAMESPACE", "#db2c18Вы указали неверный путь для предмета ")
    val ITEM_CREATE_WRONG_FLAG: String =
        translationFile.getHEXString("ITEM_CREATE_WRONG_FLAG", "#db2c18Вы ввели неверный ItemFlag")
    val ITEM_CREATE_WRONG_ATTRIBUTE: String =
        translationFile.getHEXString("ITEM_CREATE_WRONG_ATTRIBUTE", "#db2c18Вы ввели неверный Attribute")
    val ITEM_REPLACED: String = translationFile.getHEXString("ITEM_REPLACED", "#18db52Предмет заменён")
    val ITEM_REPLACE_WRONG: String =
        translationFile.getHEXString("ITEM_REPLACE_WRONG", "#db2f18Предмет не подходит для замены")

    val WRONG_ENUM: String = translationFile.getHEXString("WRONG_ENUM", "#f55442Введено несуществующее значение...")
    //Zip
    val ZIP_START: String = translationFile.getHEXString(
        "ZIP_START",
        "Начинается архивирование.\nВремя зависит от количества файлов.\nДождитесь окончания архивирования."
    )
    val ZIP_SUCCESS: String = translationFile.getHEXString("ZIP_SUCCESS", "#42f596Архивирование успешно завершено")
    val ZIP_ERROR: String = translationFile.getHEXString("ZIP_ERROR", "#f55442Ошибка при упакове ресурс-пака")
    val CLEAR_ITEMS: String = translationFile.getHEXString("CLEAR_ITEMS", "#42f596Чистятся предметы...")
    val ZIP_ITEMS: String = translationFile.getHEXString("ZIP_ITEMS", "#42f596Создаются предметы...")
    val ZIP_FONTS: String = translationFile.getHEXString("ZIP_FONTS", "#42f596Создаются фонты...")
    val ZIP_SOUND: String = translationFile.getHEXString("ZIP_SOUND", "#42f596Создаются звуки...")
    val ZIP_BLOCKS: String = translationFile.getHEXString("ZIP_BLOCKS", "#42f596Создаются блоки...")


    //Upgrades
    val ITEM_UPGRADE_NAME_COLOR: String = translationFile.getHEXString("ITEM_UPGRADE_NAME_COLOR", "#4790ad")
    val ITEM_UPGRADE_AMOUNT_COLOR: String = translationFile.getHEXString("ITEM_UPGRADE_AMOUNT_COLOR", "#47ad5f")
    val ITEM_UPGRADE_UNSUCCESFULL: String =
        translationFile.getHEXString("ITEM_UPGRADE_UNSUCCESFULL", "#f55442Улучшение не прошло успешно...")
    val ITEM_UPGRADE_SUCCESFULL: String =
        translationFile.getHEXString("ITEM_UPGRADE_SUCCESFULL", "#42f596Улучшение прошло успешно...")

    //Hooks
    val PLUGIN_PROTOCOLLIB_NOT_INSTALLED: String = translationFile.getHEXString(
        "PLUGIN_PROTOCOLLIB_NOT_INSTALLED",
        "#f55442ProtocolLib не найден. NPC не включены!"
    )

    //EmGui
    val ITEM_INFO_IMPROVING: String = translationFile.getHEXString("ITEM_INFO_IMPROVING", "Улучшает")
    val ITEM_INFO_IMPROVING_COLOR: String = translationFile.getHEXString("ITEM_INFO_IMPROVING_COLOR: String", "#47ad5f")
    val ITEM_INFO_GENERATE: String = translationFile.getHEXString("ITEM_INFO_GENERATE", "Генерируется:")
    val ITEM_INFO_VILLAGER_BUY: String =
        translationFile.getHEXString("ITEM_INFO_VILLAGER_BUY", "Можно купить у жителя:")
    val ITEM_INFO_DROP: String = translationFile.getHEXString("ITEM_INFO_DROP", "Выпадает из")
    val ITEM_INFO_DROP_COLOR: String = translationFile.getHEXString("ITEM_INFO_DROP_COLOR: String", "#47ad5f")

    //GUI
    val PREV_PAGE: String = translationFile.getHEXString("PREV_PAGE", ChatColor.GREEN.toString() + "<- Пред. страница")
    val BACK_PAGE: String = translationFile.getHEXString("BACK_PAGE", ChatColor.GREEN.toString() + "Назад")
    val NEXT_PAGE: String = translationFile.getHEXString("NEXT_PAGE", ChatColor.GREEN.toString() + "След. страница ->")

    val FIRST_PAGE: String = translationFile.getHEXString("FIRST_PAGE", "#f5a742Вы на первой странице")
    val LAST_PAGE: String = translationFile.getHEXString("LAST_PAGE", "#f5a742Вы на последней странице")
    //Mobs
    val MOB_WRON_PARSE: String = translationFile.getHEXString("MOB_WRON_PARSE", "#f55442 Ошибка при парсинге моба: ")



    //Resource Pack
    val RESOURCE_PACK_DENY: String =
        translationFile.getHEXString("RESOURCE_PACK_DENY", "#f55442Вы отклонили ресурс-пак")
    val RESOURCE_PACK_SEND: String = translationFile.getHEXString(
        "RESOURCE_PACK_SEND", "#34ebab Приветствуем на EmpireSMP!\n" +
                "#20b3c9 У нас тут куча новых предметов в /emgui Новые мобы, меню, эмодзи...\n" +
                "#20c942 Однако, чтобы видеть это - необходимо скачать наш ресурс-пак!\n" +
                "#20c99c С ним у вас возникнет ощущение, что вы играете с модами, хотя никаких модов тут нет!\n" +
                "#2085c9 У нас куча новых механик, с которыми  вам помогут разобраться игроки и администрация!\n" +
                "#c93920 Если возникнут вопросы - зайдите в наш дискорд /motd"
    )
    val RESOURCE_PACK_SEND_JSON: String = translationFile.getString(
        "RESOURCE_PACK_SEND_JSON",
        "[\"\",{\"text\":\"-----------------------------------------------------------------------\",\"color\":\"#9C0EDC\"},{\"text\":\"\\n\"},{\"text\":\"Приветствуем на EmpireSMP!\",\"color\":\"#E4CC15\"},{\"text\":\"\\n\"},{\"text\":\"У нас куча новых предметов, мобов, новые меню и эмодзи!\",\"color\":\"#2ACADF\"},{\"text\":\"\\n\"},{\"text\":\"Всё это имеет новые текстурки и модельки!\",\"color\":\"#13C93A\"},{\"text\":\"\\n\"},{\"text\":\"Играя здесь у вас возникнет ощущение игры с модами!\",\"color\":\"#62C21D\"},{\"text\":\"\\n\"},{\"text\":\"Множество новых механик:\",\"color\":\"#AF8F08\"},{\"text\":\"\\n\"},{\"text\":\"Система рангов, апгрейды, оружие, ранги, новая руда, магазины\",\"color\":\"#C7BF1B\"},{\"text\":\"\\n\"},{\"text\":\"Не забудьте зайти в дискорд, если будут какие-то вопросы! /motd\",\"color\":\"#1784D3\"},{\"text\":\"\\n\"},{\"text\":\"EmpireProjekt.ru\",\"color\":\"#CE1881\"},{\"text\":\"\\n\"},{\"text\":\"------------------------------------------------------------------------\",\"color\":\"#7000ED\"}]"
    )!!
    val RESOURCE_PACK_DOWNLOAD_SELF: String = translationFile.getHEXString(
        "RESOURCE_PACK_DOWNLOAD_SELF",
        "#f54542Попробуйте скачать самостоятельно. #429ef5empireprojekt.ru/files/EmpireProjektPack.zip"
    )

    val RESOURCE_PACK_HINT_TITLE: String =
        translationFile.getHEXString("RESOURCE_PACK_HINT_DOWNLOAD", "#db2518Скачайте ресурс-пак")
    val RESOURCE_PACK_HINT_SUBTITLE: String = translationFile.getHEXString(
        "RESOURCE_PACK_HINT_SUBTITLE",
        "#18db4fВведите #189adb/empack. #18db4fОн нужен для новых предметов, звуков, оружие, брони и т.д"
    )

    val RESOURCE_PACK_DOWNLOAD_ERROR: String = translationFile.getHEXString(
        "RESOURCE_PACK_DOWNLOAD_ERROR", """
        #f54542Не удалось загрузить ресурс-пак
        #f54542Скачайте его самостоятельно с #429ef5empireprojekt.ru/files/EmpireProjektPack.zip
        #f54542Дискорд https://discord.gg/Gwukdr8
        #f54542Группа ВК https://vk.com/EmpireProjekt
    """.trimIndent()
    )


    //NPC
    val NPC_CREATED: String = translationFile.getHEXString("NPC_CREATED", "#f5a742NPC Создан")
    val NPC_NOT_FOUND_RAYCAST: String =
        translationFile.getHEXString("NOT_FOUND_RAYCAST", "#f5a742Вы не смотрите на NPC")
    val NPC_FOUND_RAYCAST: String = translationFile.getHEXString("NPC_FOUND_RAYCAST", "#f5a742Выбран NPC")
    val NPC_NOT_WRITTEN_ID: String =
        translationFile.getHEXString("NPC_NOT_WRITTEN_ID", "#f5a742Вы не ввели ID /emnpc create NEWID")



    //Credits
    val CANT_HAVE_CREDIT: String = translationFile.getHEXString("CANT_HAVE_CREDIT", "#f5a742Вы не можете получить кредит.")
    val MIN_FOR_CREDIT: String = translationFile.getHEXString("MIN_FOR_CREDIT", "#f5a742Минимальный баланс для кредита %amount%.")
    val GAVE_CREDIT: String = translationFile.getHEXString("GAVE_CREDIT", "#f5a742Вы получили кредит: %amount%$.")
    val PAID_CREDIT: String = translationFile.getHEXString("PAID_CREDIT", "#f5a742Вы выплатили кредит %amount%$.")
    val NO_ACTIVE_CREDIT: String = translationFile.getHEXString("NO_ACTIVE_CREDIT", "#f5a742У вас нет активных кредитов.")
    val NOT_ENOUGH_MONEY: String = translationFile.getHEXString("NOT_ENOUGH_MONEY", "#f5a742У вас недостаточно денег.")
    //Bank
    val BANK_DEPOSIT: String = translationFile.getHEXString("BANK_DEPOSIT", "#f5a742Вы внесли в банк %amount%$.")
    val BANK_WITHDRAW: String = translationFile.getHEXString("BANK_WITHDRAW", "#f5a742Вы взяли из банка %amount%$.")


    //Database
    val DB_SUCCESS: String = translationFile.getHEXString("DB_SUCCESS", "#f5a742База данных успешно подключена.")
    val DB_FAIL: String = translationFile.getHEXString("DB_FAIL", "#f5a742Не удалось подключиться к базе данных.")


}