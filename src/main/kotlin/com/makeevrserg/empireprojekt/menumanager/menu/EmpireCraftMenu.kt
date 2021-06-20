package com.makeevrserg.empireprojekt.menumanager.menu


import com.makeevrserg.empireprojekt.events.CraftEvent
import com.makeevrserg.empireprojekt.events.genericlisteners.ItemDropListener
import com.makeevrserg.empireprojekt.events.ItemUpgradeEvent
import com.makeevrserg.empireprojekt.menumanager.PaginatedMenu
import com.makeevrserg.empireprojekt.menumanager.PlayerMenuUtility
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import com.makeevrserg.empireprojekt.util.EmpirePermissions
import com.makeevrserg.empireprojekt.util.EmpireUtils
import com.makeevrserg.empireprojekt.util.Translations.Companion.translations
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.persistence.PersistentDataType


class EmpireCraftMenu(
    override var playerMenuUtility: PlayerMenuUtility,
    private val slot: Int,
    private val categoryPage: Int,
    private val item: String,
    override var page: Int
) : PaginatedMenu(translations, playerMenuUtility) {

    val guiConfigFile = plugin.empireFiles.guiFile.getConfig()
    var recipePage = 0

    override var menuName: String = EmpireUtils.HEXPattern(
        plugin.empireFiles.guiFile.getConfig()
            ?.getString(
                "settings.workbench_ui",
                "Крафт"
            ) + plugin.empireItems.empireItems[item]?.itemMeta?.displayName
    )
    override var slots: Int = 54
    private fun playInventorySound() {

        playerMenuUtility.player.playSound(
            playerMenuUtility.player.location,
            guiConfigFile?.getString("settings.workbench_sound") ?: Sound.ITEM_BOOK_PAGE_TURN.name.toLowerCase(),
            1.0f,
            1.0f
        )

    }

    init {
        playInventorySound()
        maxPages = useInCraft(item).size / 9
    }

    override fun handleMenu(e: InventoryClickEvent) {
        if (e.slot == 49) {
            if (playerMenuUtility.previousItems.size == 0)
                EmpireCategoryMenu(playerMenuUtility,  slot, categoryPage).open()
            else {
                val prevId = playerMenuUtility.previousItems.last()
                playerMenuUtility.previousItems.removeAt(playerMenuUtility.previousItems.size - 1)
                EmpireCraftMenu(playerMenuUtility, slot, categoryPage,  prevId, 0).open()
            }
        } else if (arrayOf(11, 12, 13, 20, 21, 22, 29, 30, 31, 36, 37, 38, 39, 40, 41, 42, 43, 44).contains(e.slot)) {
            val id = inventory.getItem(e.slot)?.itemMeta?.persistentDataContainer?.get(
                plugin.empireConstants.empireID,
                PersistentDataType.STRING
            ) ?: return

            playerMenuUtility.previousItems.add(item)
            EmpireCraftMenu(playerMenuUtility, slot, categoryPage,  id, 0).open()
        } else if (e.slot == 34) {
            if (!playerMenuUtility.player.hasPermission("empireitems.give"))
                return
            playerMenuUtility.player.inventory
                .addItem(plugin.empireItems.empireItems[item] ?: ItemStack(Material.getMaterial(item) ?: return))
        } else if (e.slot == 53) {
            if (checkLastPage())
                return
            reloadPage(1)
        } else if (e.slot == 45) {
            if (checkFirstPage())
                return
            reloadPage(-1)
        } else if (e.slot == 7) {
            recipePage++
            setCraftingTable()
        } else if (e.slot == 8) {
            recipePage++
            setFurnaceRecipe()
        }
    }

    fun setRecipe() {

        val empireRecipie = plugin.genericListener._craftEvent.empireRecipies[item] ?: return
        if (empireRecipie.craftingTable.size > 0)
            setWorkbenchButton()
        if (empireRecipie.furnace.size > 0)
            setFurnaceButton()
    }

    fun setFurnaceRecipe() {
        val empireRecipie = plugin.genericListener._craftEvent.empireRecipies[item] ?: return
        var invPos = 11
        if (empireRecipie.furnace.size <= recipePage)
            recipePage = 0
        val recipe = empireRecipie.furnace[recipePage]
        for (i in 1..3){
            for (j in 1..3)
                inventory.clear(invPos++)
           invPos+=9-3
        }
        invPos = 11
        inventory.setItem(invPos, recipe.input)
        inventory.setItem(invPos + 9, ItemStack(Material.COAL))

    }

    fun setCraftingTable() {
        val empireRecipie = plugin.genericListener._craftEvent.empireRecipies[item] ?: return
        var invPos = 11
        if (empireRecipie.craftingTable.size <= recipePage)
            recipePage = 0

        val recipe = empireRecipie.craftingTable[recipePage]

        for (lineShape in recipe.shape) {
            for (char in lineShape) {
                inventory.setItem(invPos++, recipe.ingredientMap[char])
            }
            invPos += 9 - 3
        }


    }

    fun useInCraft(item: String): MutableList<String> {
        val itemStack = plugin.empireItems.empireItems[item] ?: return mutableListOf()
        val list = mutableListOf<String>()

        for (itemResult in plugin.genericListener._craftEvent.empireRecipies.keys) {
            val itemRecipies: CraftEvent.EmpireRecipe =plugin.genericListener._craftEvent.empireRecipies[itemResult] ?: return mutableListOf()
            for (empireRecipe in itemRecipies.craftingTable) {
                if (empireRecipe is ShapedRecipe) {
                    if (empireRecipe.ingredientMap.values.contains(itemStack)) {
                        list.add(itemResult)
                        break
                    }

                }
            }

        }
        return list

    }

    fun setCanCraft() {
        val itemsToCraft = useInCraft(item)
        var invPosition = 36
        for (invPos in 0 until 9) {
            val index = 9 * page + invPos
            if (index >= itemsToCraft.size)
                return
            inventory.setItem(invPosition++, plugin.empireItems.empireItems[itemsToCraft[index]] ?: continue)
        }
    }

    override fun setMenuItems() {

        setCraftingTable()
        setCanCraft()
        inventory.setItem(
            25,

            Bukkit.getRecipe(NamespacedKey(plugin, item))?.result ?: plugin.empireItems.empireItems[item] ?: ItemStack(
                Material.getMaterial(item) ?: Material.PAPER
            )
        )
        addManageButtons()
        inventory.setItem(
            48,
            setDrop() ?: ItemStack(Material.AIR)
        )
        inventory.setItem(
            47,
            setUpgrade() ?: ItemStack(Material.AIR)
        )
        if (playerMenuUtility.player.hasPermission(EmpirePermissions.EMPGIVE))
            inventory.setItem(
                34,
                plugin.empireItems.empireItems[plugin.empireFiles.guiFile.getConfig()
                    ?.getConfigurationSection("settings")?.getString("give_btn")] ?: ItemStack(Material.AIR)
            )


        setRecipe()
    }

    private fun setWorkbenchButton() {
        inventory.setItem(
            7, plugin.empireItems.empireItems[plugin.empireFiles.guiFile.getConfig()
                ?.getConfigurationSection("settings")?.getString("crafting_table_btn")]
        )
    }

    private fun setFurnaceButton() {
        inventory.setItem(
            8, plugin.empireItems.empireItems[plugin.empireFiles.guiFile.getConfig()
                ?.getConfigurationSection("settings")?.getString("furnace_btn")]
        )
    }

    private fun getItemStack(path: String): ItemStack {
        return plugin.empireItems.empireItems[plugin.empireFiles.guiFile.getConfig()?.getString(path)]?.clone()
            ?: ItemStack(Material.PAPER).clone()
    }


    private fun setUpgrade(): ItemStack? {
        fun containValue(value: String, list: List<String>): Boolean {
            for (str in list) {
                if (str.contains(value)) {
                    return true
                }
            }
            return false
        }

        val itemStack = getItemStack("settings.drop_btn")
        val itemMeta = itemStack.itemMeta
        val upgrades: List<ItemUpgradeEvent.ItemUpgrade> = plugin.genericListener._itemUpgradeEvent.upgradesMap[item] ?: return null
        itemMeta.setDisplayName(plugin.translations.ITEM_INFO_IMPROVING)
        val lore = itemMeta.lore ?: mutableListOf()
        for (upgrade: ItemUpgradeEvent.ItemUpgrade in upgrades) {
            if (!containValue(ItemUpgradeEvent.attrMap[upgrade.attr] ?: continue, lore))
                lore.add(plugin.translations.ITEM_INFO_IMPROVING_COLOR + "${ItemUpgradeEvent.attrMap[upgrade.attr]} [${upgrade.add_min};${upgrade.add_max}]")


        }

        itemMeta.lore = lore
        itemStack.itemMeta = itemMeta
        return itemStack

    }

    private fun setDrop(): ItemStack? {
        val itemStack = getItemStack("settings.drop_btn")
        val itemMeta = itemStack.itemMeta

        itemMeta.setDisplayName(plugin.translations.ITEM_INFO_DROP)
        val everyDropByItem: MutableMap<String, MutableList<ItemDropListener.ItemDrop>> = plugin.getEveryDrop
        everyDropByItem[item] ?: return null
        val lore = itemMeta.lore ?: mutableListOf()
        for (drop in everyDropByItem[item]!!) {
            lore.add(plugin.translations.ITEM_INFO_DROP_COLOR + "${drop.dropFrom} [${drop.minAmount};${drop.maxAmount}] ${drop.chance}%")
        }
        itemMeta.lore = lore
        itemStack.itemMeta = itemMeta
        return itemStack
    }

}





