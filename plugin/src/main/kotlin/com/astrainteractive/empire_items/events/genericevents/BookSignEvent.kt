package com.astrainteractive.empire_items.events.genericevents

import com.astrainteractive.empire_items.di.empireUtilsModule
import ru.astrainteractive.astralibs.events.EventListener
import ru.astrainteractive.astralibs.utils.convertHex
import org.bukkit.event.EventHandler
import org.bukkit.event.block.SignChangeEvent
import org.bukkit.event.player.PlayerEditBookEvent
import ru.astrainteractive.astralibs.di.getValue

class BookSignEvent:EventListener {

    val empireUtils by empireUtilsModule

    public override fun onDisable(){
        SignChangeEvent.getHandlerList().unregister(this)
        PlayerEditBookEvent.getHandlerList().unregister(this)
    }



    @EventHandler
    fun onBookEvent(e: PlayerEditBookEvent) {
        val newMeta = e.newBookMeta
        if (newMeta.hasAuthor())
            newMeta.author =
                convertHex(empireUtils.emojiPattern(newMeta.author!!))

        if (newMeta.hasTitle())
            newMeta.title =
                convertHex(empireUtils.emojiPattern(newMeta.title!!))
        for (i in 1..newMeta.pageCount) {
            newMeta.setPage(
                i, convertHex(
                    empireUtils.emojiPattern(
                        newMeta.getPage(i)
                    ) + "&r"
                )
            )
        }
        e.newBookMeta = newMeta
    }

    @EventHandler
    fun onSignEvent(e: SignChangeEvent) {

        for (i in e.lines.indices)
            e.setLine(
                i,
                convertHex(empireUtils.emojiPattern(e.getLine(i) ?: continue))
            )
    }
}