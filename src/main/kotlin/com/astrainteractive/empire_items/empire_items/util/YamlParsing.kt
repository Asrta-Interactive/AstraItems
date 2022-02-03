package com.astrainteractive.empire_items.empire_items.util

import com.astrainteractive.astralibs.Logger
import com.astrainteractive.astralibs.catching
import com.astrainteractive.astralibs.getFloat
import com.astrainteractive.empire_items.empire_items.api.font.AstraFont
import org.bukkit.configuration.ConfigurationSection
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty1
import kotlin.reflect.full.instanceParameter
import kotlin.reflect.full.memberProperties


@Target(AnnotationTarget.PROPERTY)
annotation class YamlData(
    val name: String
)

fun <V> ConfigurationSection.getYamlData(yaml: YamlData, _class: Class<V>): V? = catching {
    return when (_class) {
        Double::class ->
            getDouble(yaml.name) as V
        Int::class -> getInt(yaml.name) as V
        String::class -> getString(yaml.name) as V
        Float::class -> getFloat(yaml.name) as V
        Boolean::class -> getBoolean(yaml.name) as V
        List::class -> getList(yaml.name) as V
        else -> get(yaml.name) as V
    }
}

fun List<Annotation>.getYamlData() = this.firstOrNull() { it.annotationClass == YamlData::class }

inline fun <T, reified V> ConfigurationSection.yamlProperty(prop: KProperty1<T, V>,default:V): V {
    return yamlProperty(prop)?:default
}
inline fun <T, reified V> ConfigurationSection.yamlProperty(prop: KProperty1<T, V>): V? {
    val yamlData = prop.annotations.getYamlData() as YamlData?
    yamlData ?: return null
    val g = getYamlData(yamlData, V::class.java)
    return g
}


