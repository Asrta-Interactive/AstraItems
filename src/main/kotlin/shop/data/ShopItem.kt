package shop.data

import com.google.gson.reflect.TypeToken
import com.makeevrserg.empireprojekt.EmpirePlugin
import empirelibs.EmpireYamlParser
import makeevrserg.empireprojekt.random_items.data.RandomItem
import shop.EmpireShopManager

data class ShopItem(
    val position: Int,
    val median: Int,
    val defaultStock: Int,
    val currentStock: Int,
    val value: Int,
    val valueMin: Int,
    val valueMax: Int
) {
    companion object {
        fun new() {
            EmpireYamlParser.fromYAML<Map<String, List<ShopItem>>>(
                EmpireShopManager.files.shop.getConfig(),
                object : TypeToken<Map<String, List<ShopItem?>>?>() {}.type,
                listOf("shops")
            )
        }
    }
}