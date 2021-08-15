package empirelibs

import com.makeevrserg.empireprojekt.EmpirePlugin
import org.bukkit.event.Listener

interface IEmpireListener:Listener {


    fun onEnable(manager:IEventManager): IEmpireListener {
        EmpirePlugin.instance.server.pluginManager.registerEvents(this, EmpirePlugin.instance)
        manager.addHandler(this)
        return this
    }
    abstract fun onDisable()
}