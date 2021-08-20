package com.makeevrserg.empireprojekt.credit.commands

import com.makeevrserg.empireprojekt.EmpirePlugin

class CommandManager {

    init {
        EmpirePlugin.instance.getCommand("emcredit")!!.setExecutor(Credit())
        EmpirePlugin.instance.getCommand("embank")!!.setExecutor(Bank())
    }
}