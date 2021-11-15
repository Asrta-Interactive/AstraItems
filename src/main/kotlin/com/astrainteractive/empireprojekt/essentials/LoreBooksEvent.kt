package com.astrainteractive.empireprojekt.essentials

import com.astrainteractive.astralibs.AstraUtils
import com.astrainteractive.empireprojekt.EmpirePlugin
import com.astrainteractive.empireprojekt.empire_items.util.EmpireUtils
import org.bukkit.block.Chest
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.loot.Lootable
import kotlin.random.Random

/**
 * Книжки с лором
 * todo - передалть инициализацию и Listener
 */
class LoreBooksEvent : Listener {
    private val books: MutableList<ItemStack> = mutableListOf()


    @EventHandler
    fun onPlayerOpenNewChestEvent(e: PlayerInteractEvent) {
        if (e.action != Action.RIGHT_CLICK_BLOCK)
            return
        if (books.isEmpty())
            return

        val block = e.clickedBlock ?: return
        block.state
        if (block.state !is Chest)
            return
        val chest = block.state as Chest
        val lootable = chest as Lootable
        lootable.lootTable ?: return
        chest.blockInventory.addItem(books[Random.nextInt(books.size)])
    }


    private fun initBooks(): Boolean {
        val configSection =
            EmpirePlugin.empireFiles.loreBooks.getConfig()?.getConfigurationSection("lore_books") ?: return false

        for (bookKey in configSection.getKeys(false)) {
            val section = configSection.getConfigurationSection(bookKey)!!
            val author = AstraUtils.HEXPattern(section.getString("author") ?: continue)
            val title = AstraUtils.HEXPattern(section.getString("title") ?: continue)
            val pages = AstraUtils.HEXPattern(section.getStringList("pages"))
            val book = EmpireUtils.getBook(author, title, pages)
            books.add(book)
        }

        return true

    }


    init {
        if (initBooks())
            EmpirePlugin.instance.server.pluginManager.registerEvents(this, EmpirePlugin.instance)
    }



    fun onDisable() {
        PlayerInteractEvent.getHandlerList().unregister(this)
    }



}