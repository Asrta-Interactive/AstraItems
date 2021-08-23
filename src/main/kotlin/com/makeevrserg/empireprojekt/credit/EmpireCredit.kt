package com.makeevrserg.empireprojekt.credit

import com.earth2me.essentials.Essentials
import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.credit.commands.CommandManager
import com.makeevrserg.empireprojekt.credit.data.CreditConfig
import com.makeevrserg.empireprojekt.credit.data.CreditPlayer
import com.makeevrserg.empireprojekt.empirelibs.EmpireUtils
import com.makeevrserg.empireprojekt.empirelibs.FileManager
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitTask


class EmpireCredit {
    companion object {
        lateinit var instance: EmpireCredit
            private set
        lateinit var configFile: FileManager
            private set
        lateinit var essentials: Essentials
            private set
        lateinit var config: CreditConfig
            private set
    }

    private lateinit var placeholderHook:PlaceholderHook

    private lateinit var task: BukkitTask

    private fun initCreditSystem() {
        essentials = (Bukkit.getPluginManager().getPlugin("Essentials") ?: return) as Essentials
        instance = this
        configFile = FileManager("credit/credit.yml")
        config = CreditConfig.new()!!
        CommandManager()
        println(Bukkit.getServer().pluginManager.getPlugin("PlaceholderAPI"))
        if (Bukkit.getServer().pluginManager.getPlugin("PlaceholderAPI")!=null) {
            println("InitPlaceholder")
            placeholderHook = PlaceholderHook()
            placeholderHook.register()
        }



        task = EmpireUtils.EmpireRunnable {
            for (player in Bukkit.getOnlinePlayers()){
                if (!CreditAPI.hasCredit(player))
                    continue
                val creditPlayer = CreditPlayer.getPlayer(player)?:continue
                val timePassed = (System.currentTimeMillis()-creditPlayer.unix)/1000.0/60.0/60.0
                if (timePassed<5)
                    continue
                val amount = CreditAPI.getPercentOfCredit(player)?:continue
                CreditAPI.repayCredit(player,amount.toInt())
            }
        }.runTaskTimerAsynchronously(EmpirePlugin.instance,0,12000)

    }

    init {
        initCreditSystem()
    }
    fun onDisable(){
        if (Bukkit.getServer().pluginManager.getPlugin("PlaceholderAPI")!=null && Bukkit.getPluginManager().getPlugin("Essentials")!=null) {
            placeholderHook.unregister()
            task.cancel()
        }
    }


}