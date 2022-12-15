package com.astrainteractive.empire_items.util

import java.lang.reflect.Field

object MoreReflectedUtil {
    inline fun <reified T : Any> setFinalField(instance: T, newValue: T, fieldName: String, isDeclared: Boolean) {
        val clazz: Class<out T> = instance::class.java
        val field: Field = if (isDeclared) clazz.getDeclaredField(fieldName) else clazz.getField(fieldName)
        setFinalField(instance, clazz, field, newValue)
    }

    fun <T : Any> setFinalField(instance: T, clazz: Class<out T>, field: Field, newValue: T) {
        field.isAccessible = true
        field.set(instance, newValue)
    }
}