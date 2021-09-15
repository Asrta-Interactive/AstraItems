package com.makeevrserg.empireprojekt.empire_items.util

import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.empirelibs.EmpireYamlParser


data class EmpireSound(
    @SerializedName("id")
    val id: String,
    @SerializedName("sounds")
    val sounds: List<String>
) {
    companion object {

        fun new(): List<EmpireSound> = EmpireYamlParser.fromYAML<List<EmpireSound>>(
            EmpirePlugin.empireFiles._soundsFile.getConfig(),
            object : TypeToken<List<EmpireSound?>?>() {}.type,
            listOf("sounds")
        )?: mutableListOf()

        fun soundByID(list: List<EmpireSound>): Map<String, EmpireSound> =
            list.associateBy { it.id }


    }
}