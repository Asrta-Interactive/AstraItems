package com.makeevrserg.empireprojekt.util

import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.makeevrserg.empireprojekt.EmpirePlugin
import empirelibs.EmpireYamlParser


data class EmpireSound(
    @SerializedName("id")
    val id:String,
    @SerializedName("sounds")
    val sounds: List<String>
){
    companion object {

        fun new(): List<EmpireSound>  = EmpireYamlParser.fromYAML<List<EmpireSound>>(
                EmpirePlugin.empireFiles._soundsFile.getConfig(),
                object : TypeToken<List<EmpireSound?>?>() {}.type,
                listOf("sounds")
            )!!
        fun soundByID(list:List<EmpireSound>):Map<String,EmpireSound>{
            val map = mutableMapOf<String,EmpireSound>()
            list.forEach { sound ->
                map[sound.id] = sound
            }
            return  map
        }

    }
}