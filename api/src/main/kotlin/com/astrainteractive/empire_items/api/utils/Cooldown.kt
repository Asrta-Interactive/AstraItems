package com.astrainteractive.empire_items.api.utils


/**
 * Calculating cooldown for anything
 */
class Cooldown<K> {
    private val map = mutableMapOf<K, Long>()
    fun setCooldown(key: K) {
        map[key] = System.currentTimeMillis()
    }
    fun hasCooldown(key: K,time: Int?) = hasCooldown(key,time?.toLong())
    fun hasCooldown(key: K, time: Long?=null): Boolean {
        val started = map[key] ?: return false
        time?:return true
        if ((System.currentTimeMillis() - started) > time) {
            map.remove(key)
            return false
        }
        return true
    }
}