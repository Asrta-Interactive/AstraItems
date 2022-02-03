package com.astrainteractive.empire_items.empire_items.events.genericevents

import com.astrainteractive.astralibs.EventListener
import com.astrainteractive.astralibs.convertHex
import com.astrainteractive.empire_items.empire_items.util.EmpireUtils
import org.bukkit.event.EventHandler
import org.bukkit.event.block.SignChangeEvent
import org.bukkit.event.player.PlayerEditBookEvent

class BookSignEvent:EventListener {


    public override fun onDisable(){
        SignChangeEvent.getHandlerList().unregister(this)
        PlayerEditBookEvent.getHandlerList().unregister(this)
    }



    @EventHandler
    fun onBookEvent(e: PlayerEditBookEvent) {
        val newMeta = e.newBookMeta
        if (newMeta.hasAuthor())
            newMeta.author =
                convertHex(EmpireUtils.emojiPattern(newMeta.author!!))

        if (newMeta.hasTitle())
            newMeta.title =
                convertHex(EmpireUtils.emojiPattern(newMeta.title!!))
        for (i in 1..newMeta.pageCount) {
            newMeta.setPage(
                i, convertHex(
                    EmpireUtils.emojiPattern(
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
                convertHex(EmpireUtils.emojiPattern(e.getLine(i) ?: continue))
            )
    }
}