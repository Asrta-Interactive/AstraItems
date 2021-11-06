package com.astrainteractive.empireprojekt.credit.commands

import com.astrainteractive.empireprojekt.EmpirePlugin

class CommandManager {

    init {
        EmpirePlugin.instance.getCommand("emcredit")!!.setExecutor(Credit())
        EmpirePlugin.instance.getCommand("embank")!!.setExecutor(Bank())
    }
}