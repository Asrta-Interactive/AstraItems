package com.makeevrserg.empireprojekt.essentials

import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.EmpirePlugin.Companion.plugin
import com.makeevrserg.empireprojekt.util.EmpireUtils
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.Chest
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BookMeta
import org.bukkit.loot.Lootable
import kotlin.random.Random

class LoreBooks : Listener {
    val books: MutableList<ItemStack> = mutableListOf()

    init {
        if (initBooks())
            plugin.server.pluginManager.registerEvents(this, EmpirePlugin.plugin)

    }

    fun onDisable() {
        PlayerInteractEvent.getHandlerList().unregister(this)
    }


    @EventHandler
    fun onPlayerOpenNewChestEvent(e:PlayerInteractEvent){
        if (e.action!=Action.RIGHT_CLICK_BLOCK)
            return
        if (books.isEmpty())
            return

        val player = e.player
        val block = e.clickedBlock?:return
        block.state?:return
        if (block.state !is Chest)
            return
        val chest = block.state as Chest
        val lootable = chest as Lootable
        lootable.lootTable?:return
        chest.blockInventory.addItem(books[Random.nextInt(books.size)])
    }


    private fun initBooks(): Boolean {
        val configSection =
            plugin.empireFiles.loreBooks.getConfig()?.getConfigurationSection("lore_books") ?: return false

        for (bookKey in configSection.getKeys(false)) {
            val section = configSection.getConfigurationSection(bookKey)!!
            val author = EmpireUtils.HEXPattern(section.getString("author") ?: continue)
            val title = EmpireUtils.HEXPattern(section.getString("title") ?: continue)
            val pages = EmpireUtils.HEXPattern(section.getStringList("pages") ?: continue)
            val book = EmpireUtils.getBook(author, title, pages)
            books.add(book)
        }

        return true

    }


}