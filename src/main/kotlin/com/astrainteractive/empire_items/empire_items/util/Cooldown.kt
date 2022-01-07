package com.astrainteractive.empire_items.empire_items.util


/**
 * Calculating cooldown for anything
 */
class Cooldown<K> {
    private val map = mutableMapOf<K, Long>()
    fun setCooldown(key: K) {
        map[key] = System.currentTimeMillis()
    }

    fun hasCooldown(key: K,time: Int) = hasCooldown(key,time.toLong())
    fun hasCooldown(key: K, time: Long?): Boolean {
        time?:return false
        val started = map[key] ?: return false
        if ((System.currentTimeMillis() - started) > time) {
            map.remove(key)
            return true
        }
        return false
    }
}