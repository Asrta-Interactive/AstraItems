//package com.makeevrserg.empireprojekt.menumanager.emgui
//
//import org.bukkit.configuration.ConfigurationSection
//
//class CategoryItems(categoriesSection: ConfigurationSection?) {
//    data class CategorySection(
//        val title: String,
//        val name: String,
//        val icon: String,
//        val lore: MutableList<String>,
//        val items: List<String>
//    )
//
//    public val categoriesMap: MutableMap<String, CategorySection> = mutableMapOf()
//
//    init {
//        initCategories(categoriesSection)
//    }
//
//    private fun initCategories(categoriesSection: ConfigurationSection?) {
//        categoriesSection?:return
//        for (categoryID in categoriesSection.getKeys(false)) {
//            val section = categoriesSection.getConfigurationSection(categoryID)!!
//            categoriesMap[categoryID] =
//                CategorySection(
//                    section.getString("title", "Титул")!!,
//                    section.getString("name", "Титул")!!,
//                    section.getString("icon", "PAPER")!!,
//                    section.getStringList("lore"),
//                    section.getStringList("items")
//                )
//        }
//    }
//
//
//}