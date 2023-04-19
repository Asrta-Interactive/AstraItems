package com.astrainteractive.empire_items.plugin

import ru.astrainteractive.astralibs.utils.IPermission

sealed class Permission(override val value: String) : IPermission {
    object GiveCustomItem : Permission("empireitems.give")
    object Credit : Permission("empirecredit")
    object Bank : Permission("embank")
    object Zip : Permission("empireitems.zip")
    object SpawnModel : Permission("empireitems.spawn")
    object Reload : Permission("empireitems.reload")
    object ChangeFlySpeed : Permission("empireitems.espeed")
    object SpawnEntity : Permission("empireitems.spawn_entity")
}