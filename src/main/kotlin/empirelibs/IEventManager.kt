package empirelibs

interface IEventManager {
    abstract val handlers: MutableList<IEmpireListener>
    public fun addHandler(event: IEmpireListener) {
        handlers.add(event)
    }
    public fun onDisable() {
        handlers.forEach {
            it.onDisable()
        }
    }
}