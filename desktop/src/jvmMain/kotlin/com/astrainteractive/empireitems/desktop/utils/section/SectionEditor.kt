package com.astrainteractive.empireitems.desktop.utils.section

import kotlinx.coroutines.flow.MutableStateFlow

interface SectionEditor<T> {

    val state: MutableStateFlow<T>
    fun onUpdate(it: T) {
        state.value = it
    }
}