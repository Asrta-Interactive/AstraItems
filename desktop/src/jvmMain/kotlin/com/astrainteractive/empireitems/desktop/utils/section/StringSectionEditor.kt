package com.astrainteractive.empireitems.desktop.utils.section

import kotlinx.coroutines.flow.MutableStateFlow

class StringSectionEditor(initial: String): SectionEditor<String> {
    override val state = MutableStateFlow<String>(initial)

}