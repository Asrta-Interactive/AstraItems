package com.makeevrserg.empireprojekt.menumanager.emgui


import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.util.CraftEvent
import com.makeevrserg.empireprojekt.events.genericevents.ItemDropListener
import com.makeevrserg.empireprojekt.events.ItemUpgradeEvent
import com.makeevrserg.empireprojekt.menumanager.PaginatedMenu
import com.makeevrserg.empireprojekt.menumanager.PlayerMenuUtility
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import com.makeevrserg.empireprojekt.util.EmpirePermissions
import com.makeevrserg.empireprojekt.util.EmpireUtils
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.Sound


class EmpireCraftMenu(
    override var playerMenuUtility: PlayerMenuUtility,
    private val slot: Int,
    private val categoryPage: Int,
    private val item: String,
    override var page: Int
) : PaginatedMenu(playerMenuUtility) {

    //    private val guiConfigFile = EmpirePlugin.empireFiles.guiFile.getConfig()
    var recipePage = 0

    override var maxItemsPerPage: Int = 9
    override var menuSize: Int = 54
    override var slotsAmount: Int = useInCraft(item).size
    override var maxPages: Int = getMaxPages()


    private fun getItemName(): String {
        val meta = EmpirePlugin.empireItems.empireItems[item]?.itemMeta
        return meta?.displayName ?: item
    }

    override var menuName: String = EmpireUtils.HEXPattern(
        EmpirePlugin.empireFiles.guiFile.getConfig()
            ?.getString(
                "settings.workbench_ui",
                "Крафт"
            ) + (getItemName())
    )

    private fun playInventorySound() {
        playerMenuUtility.player.playSound(
            playerMenuUtility.player.location,
            EmpirePlugin.instance.guiSettings.workbenchSound,
            1.0f,
            1.0f
        )

    }

    init {
        playInventorySound()
    }

    override fun handleMenu(e: InventoryClickEvent) {
        if (e.slot == getBackButtonIndex()) {
            if (playerMenuUtility.previousItems.size == 0)
                EmpireCategoryMenu(playerMenuUtility, slot, categoryPage).open()
            else {
                val prevId = playerMenuUtility.previousItems.last()
                playerMenuUtility.previousItems.removeAt(playerMenuUtility.previousItems.size - 1)
                EmpireCraftMenu(playerMenuUtility, slot, categoryPage, prevId, 0).open()
            }
        } else if (arrayOf(11, 12, 13, 20, 21, 22, 29, 30, 31, 36, 37, 38, 39, 40, 41, 42, 43, 44).contains(e.slot)) {
            val itemStack = inventory.getItem(e.slot) ?: return
            val id = EmpireUtils.getEmpireID(itemStack) ?: itemStack.type.name
            playerMenuUtility.previousItems.add(item)
            EmpireCraftMenu(playerMenuUtility, slot, categoryPage, id, 0).open()

        } else if (e.slot == 34) {
            if (!playerMenuUtility.player.hasPermission("empireitems.give"))
                return
            playerMenuUtility.player.inventory
                .addItem(EmpirePlugin.empireItems.empireItems[item] ?: ItemStack(Material.getMaterial(item) ?: return))
        } else if (e.slot == getNextButtonIndex()) {
            if (isLastPage())
                return
            loadPage(1)
        } else if (e.slot == getPrevButtonIndex()) {
            if (isFirstPage())
                return
            loadPage(-1)
        } else if (e.slot == 7) {
            recipePage++
            setCraftingTable()
        } else if (e.slot == 8) {
            recipePage++
            setFurnaceRecipe()
        }
    }

    private fun setRecipe() {
        val empireRecipie = EmpirePlugin.instance.recipies[item] ?: return
        if (empireRecipie.craftingTable.size > 0)
            setWorkbenchButton()
        if (empireRecipie.furnace.size > 0)
            setFurnaceButton()
    }

    private fun setFurnaceRecipe() {
        val empireRecipie = EmpirePlugin.instance.recipies[item] ?: return
        var invPos = 11
        if (empireRecipie.furnace.size <= recipePage)
            recipePage = 0
        val recipe = empireRecipie.furnace[recipePage]
        for (i in 1..3) {
            for (j in 1..3)
                inventory.clear(invPos++)
            invPos += 9 - 3
        }
        invPos = 11
        inventory.setItem(invPos, recipe.input)
        inventory.setItem(invPos + 9, ItemStack(Material.COAL))

    }

    private fun setCraftingTable() {
        val empireRecipie = EmpirePlugin.instance.recipies[item] ?: return
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

    private fun useInCraft(item: String): MutableList<String> {
        val itemStack = EmpirePlugin.empireItems.empireItems[item] ?: ItemStack(
            Material.getMaterial(item) ?: return mutableListOf()
        )
        val list = mutableListOf<String>()

        for (itemResult in EmpirePlugin.instance.recipies.keys) {
            val itemRecipies: CraftEvent.EmpireRecipe =
                EmpirePlugin.instance.recipies[itemResult] ?: continue
            for (empireRecipe in itemRecipies.craftingTable) {
                if (empireRecipe.ingredientMap.values.contains(itemStack)) {
                    list.add(itemResult)
                    //break
                }
            }
        }
        return list

    }


    private fun setCanCraft() {
        val itemsToCraft = useInCraft(item)
        var invPosition = 36
        for (invPos in 0 until 9) {
            val index = 9 * page + invPos
            if (index >= itemsToCraft.size)
                return
            inventory.setItem(
                invPosition++,
                EmpirePlugin.empireItems.empireItems[itemsToCraft[index]]
                    ?: ItemStack(Material.getMaterial(itemsToCraft[index]) ?: continue)
            )
        }
    }


    private fun setWorkbenchButton() {
        inventory.setItem(
            7, EmpirePlugin.empireItems.empireItems[EmpirePlugin.empireFiles.guiFile.getConfig()
                ?.getConfigurationSection("settings")?.getString("crafting_table_btn")]
        )
    }

    private fun setFurnaceButton() {
        inventory.setItem(
            8, EmpirePlugin.empireItems.empireItems[EmpirePlugin.empireFiles.guiFile.getConfig()
                ?.getConfigurationSection("settings")?.getString("furnace_btn")]
        )
    }

    private fun getItemStack(path: String): ItemStack {
        return EmpirePlugin.empireItems.empireItems[EmpirePlugin.empireFiles.guiFile.getConfig()
            ?.getString(path)]?.clone()
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
        val upgrades: List<ItemUpgradeEvent.ItemUpgrade> = EmpirePlugin.instance.upgradesMap[item] ?: return null
        itemMeta!!.setDisplayName(EmpirePlugin.translations.ITEM_INFO_IMPROVING)
        val lore = itemMeta.lore ?: mutableListOf()
        for (upgrade: ItemUpgradeEvent.ItemUpgrade in upgrades) {
            if (!containValue(ItemUpgradeEvent.attrMap[upgrade.attr] ?: continue, lore))
                lore.add(EmpirePlugin.translations.ITEM_INFO_IMPROVING_COLOR + "${ItemUpgradeEvent.attrMap[upgrade.attr]} [${upgrade.add_min};${upgrade.add_max}]")


        }

        itemMeta.lore = lore
        itemStack.itemMeta = itemMeta
        return itemStack.clone()


    }

    private fun setDrop(): ItemStack? {
        val itemStack = getItemStack("settings.drop_btn")
        val itemMeta = itemStack.itemMeta

        itemMeta!!.setDisplayName(EmpirePlugin.translations.ITEM_INFO_DROP)
        val everyDropByItem: MutableMap<String, MutableList<ItemDropListener.ItemDrop>> =
            EmpirePlugin.instance.getEveryDrop
        everyDropByItem[item] ?: return null
        val lore = itemMeta.lore ?: mutableListOf()
        for (drop in everyDropByItem[item]!!) {
            lore.add(EmpirePlugin.translations.ITEM_INFO_DROP_COLOR + "${drop.dropFrom} [${drop.minAmount};${drop.maxAmount}] ${drop.chance}%")
        }
        itemMeta.lore = lore
        itemStack.itemMeta = itemMeta
        return itemStack
    }

    override fun setMenuItems() {

        setCraftingTable()
        setCanCraft()
        inventory.setItem(
            25,

            Bukkit.getRecipe(NamespacedKey(EmpirePlugin.instance, item))?.result
                ?: EmpirePlugin.empireItems.empireItems[item]
                ?: ItemStack(
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
                EmpirePlugin.empireItems.empireItems[EmpirePlugin.empireFiles.guiFile.getConfig()
                    ?.getConfigurationSection("settings")?.getString("give_btn")] ?: ItemStack(Material.AIR)
            )


        setRecipe()
    }
}





