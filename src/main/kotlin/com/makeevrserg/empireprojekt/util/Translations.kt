package com.makeevrserg.empireprojekt.util

import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.util.files.FileManager
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.FileConfiguration
import java.io.File


class Translations {

    private val plugin = EmpirePlugin.instance
    private val _translationFile: FileManager =
        FileManager("config" + File.separator + "translations.yml")

    private val text = _translationFile.getConfig()!!
    fun FileConfiguration.getHEXString(path: String, def: String): String {
        return EmpireUtils.HEXPattern(getString(path, def)!!)
    }
    fun ConfigurationSection.getHEXString(path: String, def: String): String {
        return EmpireUtils.HEXPattern(getString(path, def)!!)
    }


    val PLUGIN_PREFIX: String = text.getHEXString("PLUGIN_PREFIX", "#18dbd1[EmpireItems]")
    val RELOAD: String = text.getHEXString("RELOAD", "#dbbb18Перезагрузка плагина")
    val RELOAD_COMPLETE: String = text.getHEXString("RELOAD_COMPLETE", "#42f596Перезагрузка успешно завершена")
    val SAVE_ERROR: String =
        text.getHEXString("SAVE_ERROR", "#db2c18Не удалось сохранить файл")
    val NONSTANDART_FILE: String = text.getHEXString("NONSTANDART_FILE", "#db2c18Нестнадартный файл")
    val LOADING_FILE: String =
        text.getHEXString("LOADING_FILE", "#18dbd1Загрузка файла")
    val NOT_EXIST_FILE: String = text.getHEXString("NOT_EXIST_FILE", "#db2c18Файл не существует")

    val PLUGIN_WRONG_SYNTAX_ITEM: String = text.getHEXString("PLUGIN_WRONG_SYNTAX_ITEM", "#db2c18Ошибка при загрузке файла. Вероятно, указаны неверные параметры предмета.")
    val EXISTED_CUSTOM_MODEL_DATA: String = text.getHEXString("EXISTED_CUSTOM_MODEL_DATA", "#db2c18Внимание! Введене уже существующая custom_model_data.")

    val SIT_IN_AIR: String = text.getHEXString("SIT_IN_AIR", "#dbbb18Вы в воздухе")
    val SIT_ALREADY: String = text.getHEXString("SIT_ALREADY", "#dbbb18Вы уже сидите")

    val ITEM_GAINED: String = text.getHEXString("ITEM_GAINED", "#18dbd1Вы получили")

    val ITEM_GIVE: String = text.getHEXString("ITEM_GIVE", "#18dbd1Вы выдали")
    val ITEM_NOT_FOUND: String = text.getHEXString("ITEM_NOT_FOUND", "#db2c18Нет предмета")

    val WRONG_NUMBER: String = text.getHEXString("WRONG_NUMBER", "#db2c18Количество задано неверно")

    val ITEM_CREATE_WRONG_FLAG: String = text.getHEXString("ITEM_CREATE_WRONG_FLAG", "#db2c18Вы ввели неверный ItemFlag")
    val ITEM_CREATE_WRONG_ATTRIBUTE: String = text.getHEXString("ITEM_CREATE_WRONG_ATTRIBUTE", "#db2c18Вы ввели неверный Attribute")

    val ITEM_REPLACED: String = text.getHEXString("ITEM_REPLACED", "#18db52Предмет заменён")
    val ITEM_REPLACE_WRONG: String =
        text.getHEXString("ITEM_REPLACE_WRONG", "#db2f18Предмет не подходит для замены")
    val ZIP_START: String = text.getHEXString(
        "ZIP_START",
        "Начинается архивирование.\nВремя зависит от количества файлов.\nДождитесь окончания архивирования."
    )
    val ZIP_SUCCESS: String = text.getHEXString("ZIP_SUCCESS", "#42f596Архивирование успешно завершено")
    val ZIP_ERROR: String = text.getHEXString("ZIP_ERROR", "#f55442Ошибка при упакове ресурс-пака")
    val ZIP_ITEMS: String = text.getHEXString("ZIP_ITEMS", "#42f596Создаются предметы...")
    val ZIP_FONTS: String = text.getHEXString("ZIP_FONTS", "#42f596Создаются фонты...")
    val ZIP_SOUND: String = text.getHEXString("ZIP_SOUND", "#42f596Создаются звуки...")
    val ZIP_BLOCKS: String = text.getHEXString("ZIP_BLOCKS", "#42f596Создаются блоки...")


    val ITEM_UPGRADE_NAME_COLOR: String = text.getHEXString("ITEM_UPGRADE_NAME_COLOR", "#4790ad")
    val ITEM_UPGRADE_AMOUNT_COLOR: String = text.getHEXString("ITEM_UPGRADE_AMOUNT_COLOR", "#47ad5f")

    val ITEM_INFO_IMPROVING: String = text.getHEXString("ITEM_INFO_IMPROVING", "Улучшает")
    val ITEM_INFO_IMPROVING_COLOR: String = text.getHEXString("ITEM_INFO_IMPROVING_COLOR: String", "#47ad5f")

    val ITEM_INFO_DROP: String = text.getHEXString("ITEM_INFO_DROP", "Выпадает из")
    val ITEM_INFO_DROP_COLOR: String = text.getHEXString("ITEM_INFO_DROP_COLOR: String", "#47ad5f")


    val RESOURCE_PACK_DENY: String = text.getHEXString("RESOURCE_PACK_DENY", "#f55442Вы отклонили ресурс-пак")
    val RESOURCE_PACK_DOWNLOAD_SELF: String = text.getHEXString(
        "RESOURCE_PACK_DOWNLOAD_SELF",
        "#f54542Попробуйте скачать самостоятельно. #429ef5empireprojekt.ru/files/EmpireProjektPack.zip"
    )

    val RESOURCE_PACK_HINT_TITLE: String =
        text.getHEXString("RESOURCE_PACK_HINT_DOWNLOAD", "#db2518Скачайте ресурс-пак")
    val RESOURCE_PACK_HINT_SUBTITLE: String = text.getHEXString(
        "RESOURCE_PACK_HINT_SUBTITLE",
        "#18db4fВведите #189adb/empack. #18db4fОн нужен для новых предметов, звуков, оружие, брони и т.д"
    )

    val RESOURCE_PACK_DOWNLOAD_ERROR: String = text.getHEXString(
        "RESOURCE_PACK_DOWNLOAD_ERROR", """
        #f54542Не удалось загрузить ресурс-пак
        #f54542Скачайте его самостоятельно с #429ef5empireprojekt.ru/files/EmpireProjektPack.zip
        #f54542Дискорд https://discord.gg/Gwukdr8
        #f54542Группа ВК https://vk.com/EmpireProjekt
    """.trimIndent()
    )
    val FIRST_PAGE: String = text.getHEXString("FIRST_PAGE", "#f5a742Вы на первой странице")
    val LAST_PAGE: String = text.getHEXString("LAST_PAGE", "#f5a742Вы на последней странице")



    val NPC_CREATED: String = text.getHEXString("NPC_CREATED", "#f5a742NPC Создан")
    val NPC_NOT_FOUND_RAYCAST: String = text.getHEXString("NOT_FOUND_RAYCAST", "#f5a742Вы не смотрите на NPC")
    val NPC_FOUND_RAYCAST: String = text.getHEXString("NPC_FOUND_RAYCAST", "#f5a742Выбран NPC")
    val NPC_NOT_WRITTEN_ID: String = text.getHEXString("NPC_NOT_WRITTEN_ID", "#f5a742Вы не ввели ID /emnpc create NEWID")


    val SKIN_VALUE_NULL: String = text.getHEXString("SKIN_VALUE_NULL", "#f5a742Введите имя игрока для выбора скина")


}