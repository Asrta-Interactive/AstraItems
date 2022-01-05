package com.astrainteractive.empire_items.credit.commands

import com.astrainteractive.empire_items.EmpirePlugin

class CommandManager {

    init {
        EmpirePlugin.instance.getCommand("emcredit")!!.setExecutor(Credit())
        EmpirePlugin.instance.getCommand("embank")!!.setExecutor(Bank())
    }
}