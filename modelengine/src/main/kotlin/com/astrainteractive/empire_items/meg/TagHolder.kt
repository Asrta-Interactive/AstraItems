package com.astrainteractive.empire_items.meg

class TagHolder<T, K> {
    private val _map = HashMap<T, K>()
    val map: Map<T, K>
        get() = _map

    fun put(key: T, value: K) {
        _map[key] = value
    }

    fun get(key: T) = _map[key]
    fun update(key: T, block: () -> K) {
        _map[key] = block()
    }

    fun remove(key: T) = _map.remove(key)
}
