package com.astrainteractive.empire_items.api

import ru.astrainteractive.astralibs.di.IDependency
import ru.astrainteractive.astralibs.di.getValue

class FontApi(
    empireItemsApi: IDependency<EmpireItemsAPI>
) {
    private val empireItemsApi by empireItemsApi

    fun playerFonts() = empireItemsApi.fontByID.filter { !it.value.blockSend }
}