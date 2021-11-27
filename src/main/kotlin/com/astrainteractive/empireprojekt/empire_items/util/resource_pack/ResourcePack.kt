package com.astrainteractive.empireprojekt.empire_items.util.resource_pack

import com.astrainteractive.astralibs.AstraLibs
import com.astrainteractive.empireprojekt.empire_items.api.font.FontManager
import com.astrainteractive.empireprojekt.empire_items.api.items.BlockParser
import com.astrainteractive.empireprojekt.empire_items.api.items.data.AstraItem
import com.astrainteractive.empireprojekt.empire_items.api.items.data.ItemManager
import com.astrainteractive.empireprojekt.empire_items.api.sounds.AstraSounds
import com.astrainteractive.empireprojekt.empire_items.util.resource_pack.data.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import org.bukkit.Material
import java.io.File
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.StandardCopyOption

class ResourcePack {


    private fun generateFont() {
        val file = File(getFontPath() + File.separator + "default.json")
        file.createNewFile()
        val defaultFileText = InputStreamReader(AstraLibs.instance.getResource("pack/default.json")!!).readText()
        saveFileFromResources("pack/negative_spaces.ttf", getFontPath() + File.separator + "negative_spaces.ttf")
        val providers = Gson().fromJson(defaultFileText, Providers::class.java)
        FontManager.allFonts().forEach {
            val p = Providers.Provider.fromAstraFont(it)
            providers.providers.add(p)
        }
        val json = Gson().toJson(providers)
        file.writeText(setPrettyString(json))
    }

    private fun generateSounds() {
        val sounds = AstraSounds.getSounds()

        val file = File(getAssetsFolder() + sep + sounds.first().namespace + sep + "sounds.json")
        val map = sounds.associateBy { it.id }
        file.writeText(setPrettyString(Gson().toJson(map)))
    }

    private fun mkdirs() {
        createDirectory(getAssetsFolder())
        createDirectory(getMinecraftAssetsPath())
        recreateDirectory(getMinecraftAssetsPath() + sep +"blockstates")
        recreateDirectory(getMinecraftAssetsPath() + sep +"font")
        recreateDirectory(getMinecraftAssetsPath() + sep +"models${sep}item${sep}base")
        recreateDirectory(getMinecraftAssetsPath() + sep +"models${sep}block${sep}base")
        recreateDirectory(getMinecraftAssetsPath() + sep +"models${sep}block${sep}original")

        File(getMinecraftAssetsPath()).mkdirs()
        File(getMinecraftModelsPath()).mkdirs()
        File(getMinecraftItemsModelsPath()).mkdirs()
        File(getMinecraftBlocksModelsPath()).mkdirs()
        File(getFontPath()).mkdirs()
    }

    private fun resolveParent(item: AstraItem): String {
        val name = item.material.name
        return when {
            name.equals("BOW", ignoreCase = true) -> "minecraft:item/base/bow"
            name.contains("SWORD", ignoreCase = true) -> "minecraft:item/handheld"
            name.contains("SHIELD") -> "minecraft:item/handheld"
            item.block != null -> "minecraft:block/base/block_real"
            item.generate -> "minecraft:item/generated"
            else -> "minecraft:item/handheld"
        }
    }

    private fun resolvePotion(item: AstraItem) =
        if (item.material.name.contains("potion", ignoreCase = true)) "minecraft:item/potion" else null

    private fun createAutoGeneratedModel(item: AstraItem) {
        if (item.customModelData == null || item.customModelData == 0)
            return
        if (item.texturePath == null && item.modelPath == null)
            return

        fun writeModel(file: String, model: Model) {
            val file = File(file)
            file.writeText(setPrettyString(Gson().toJson(model)))
        }

        val path = File(getAutoGeneratedPath(item.namespace) + File.separator).apply { mkdirs() }
        var model = Model(resolveParent(item), Textures(layer0 = "${item.namespace}:${item.texturePath}"))
        if (item.block != null) {
            model.textures?.layer0 = null
        }
        writeModel(path.path + File.separator + "${item.id}.json", model)
        if (item.material == Material.BOW) {
            for (i in 0..2) {
                model = Model(resolveParent(item), Textures(layer0 = "${item.namespace}:${item.texturePath}_$i"))
                writeModel(path.path + File.separator + "${item.id}_$i.json", model)
            }
        }
    }

    fun createMinecraftModel(item: AstraItem) {
        if (item.customModelData == null || item.customModelData == 0)
            return
        if (item.texturePath == null && item.modelPath == null)
            return

        fun resolveModel() =
            if (item.modelPath != null) "${item.namespace}:${item.modelPath}" else "${item.namespace}:auto_generated/${item.id}"

        val file = File(getMinecraftItemsModelsPath() + sep + item.material.name.lowercase() + ".json")
        if (item.material == Material.BOW) {
            if (!file.exists())
                saveFileFromResources("pack/base/item/bow.json", getMinecraftItemsModelsPath() + sep + "bow.json")
        } else if (item.material == Material.SHIELD) {
            if (!file.exists()) {
                saveFileFromResources("pack/base/item/shield.json", getMinecraftItemsModelsPath() + sep + "shield.json")
                saveFileFromResources(
                    "pack/base/item/shield_blocking.json",
                    getMinecraftItemsModelsPath() + sep + "shield_blocking.json"
                )
            }
        } else if (item.material == Material.POTION) {
            if (!file.exists())
                saveFileFromResources("pack/base/item/potion.json", getMinecraftItemsModelsPath() + sep + "potion.json")
        }
        if (!file.exists())
            file.createNewFile()
        var model: Model? = Gson().fromJson(file.readText(), Model::class.java)
        if (model == null) {
            model = Model(
                if (item.block == null) resolveParent(item) else "minecraft:block/${item.material.name.lowercase()}",
                textures = Textures(
                    layer0 = "minecraft:item/${item.material.name.lowercase()}"
                )
            )
        }
        var predicate =
            Predicate(customModelData = item.customModelData, pulling = if (item.material == Material.BOW) 0 else null)
        var override = Override(model = resolveModel(), predicate = predicate)
        if (item.block != null) {
            model.textures = null
        }
        if (model.overrides == null) {
            model.overrides = listOf(override).toMutableList()
        } else
            model.overrides?.add(override)
        if (item.material == Material.SHIELD) {
            model.overrides?.removeLast()
            for (i in 0..1) {
                predicate = Predicate(customModelData = item.customModelData, blocking = i)
                override = Override(model = resolveModel(), predicate = predicate)
                model.overrides?.add(override)
            }
        }

        if (item.material == Material.BOW) {
            predicate = Predicate(customModelData = item.customModelData, pulling = 1)
            override = Override(model = resolveModel() + "_0", predicate = predicate)
            model.overrides?.add(override)
            predicate = Predicate(customModelData = item.customModelData, pulling = 1, pull = 0.65)
            override = Override(model = resolveModel() + "_1", predicate = predicate)
            model.overrides?.add(override)
            predicate = Predicate(customModelData = item.customModelData, pulling = 1, pull = 0.9)
            override = Override(model = resolveModel() + "_2", predicate = predicate)
            model.overrides?.add(override)

        }
        file.writeText((Gson().toJson(model)))


    }

    private fun generateItems() {
        File(getMinecraftItemsModelsPath()).delete()
        ItemManager.getSimpleItems().forEach { item ->
            if (item.texturePath != null)
                createAutoGeneratedModel(item)
            createMinecraftModel(item)
        }
    }

    fun initBaseDirectory() {
        val itemBasePath = getMinecraftItemsModelsPath() + sep + "base"
        File(getMinecraftItemsModelsPath()).listFiles()?.forEach { it.delete() }
        File(itemBasePath).apply {
            mkdirs()
        }

        saveFileFromResources("pack/base/item/bow.json", itemBasePath + sep + "bow.json")
        saveFileFromResources("pack/base/item/shield.json", itemBasePath + sep + "shield.json")
        saveFileFromResources("pack/base/item/shield_blocking.json", itemBasePath + sep + "shield_blocking.json")

        File(getMinecraftBlocksModelsPath() + sep + "base" + sep).apply {
            mkdirs()
        }
        saveFileFromResources(
            "pack/base/block/base/block_real.json",
            getMinecraftBlocksModelsPath() + sep + "base" + sep + "block_real.json"
        )
        File(getMinecraftBlocksModelsPath() + sep + "original" + sep).apply {
            mkdirs()
        }
        saveFileFromResources(
            "pack/base/block/original/brown_mushroom_block_true.json",
            getMinecraftBlocksModelsPath() + sep + "original" + sep + "brown_mushroom_block_true.json"
        )
        saveFileFromResources(
            "pack/base/block/original/mushroom_stem_true.json",
            getMinecraftBlocksModelsPath() + sep + "original" + sep + "mushroom_stem_true.json"
        )
        saveFileFromResources(
            "pack/base/block/original/red_mushroom_block_true.json",
            getMinecraftBlocksModelsPath() + sep + "original" + sep + "red_mushroom_block_true.json"
        )
    }

    fun sortItems() {
        File(getMinecraftItemsModelsPath()).listFiles()?.forEach {
            if (!it.isFile)
                return@forEach
            val model: Model? = Gson().fromJson(it.readText(), Model::class.java) ?: return@forEach
            model?.overrides?.sortBy { it.predicate.customModelData }
            it.writeText(setPrettyString(Gson().toJson(model)))
        }
    }

    fun generateBlocks() {
        val blocks = ItemManager.getBlocksInfos()
        val existedData = blocks.map { it.block!!.data }
        for (i in 0..64 * 3 - 1) {
            if (existedData.contains(i))
                continue
            val materialName = BlockParser.getMaterialByData(i).name.lowercase()
            val multipart = Multipart(
                BlockParser.getFacingByData(i),
                Apply("minecraft:block/original/${materialName}_true")
            )
            File(getMinecraftAssetsPath() + sep + "blockstates" + sep).apply { mkdirs() }
            val file = File(getMinecraftAssetsPath() + sep + "blockstates" + sep + materialName + ".json")
            if (!file.exists())
                file.createNewFile()

            var blockModels = Gson().fromJson(file.readText(), BlockModel::class.java)
            if (blockModels == null)
                blockModels = BlockModel()
            blockModels.multipart.add(multipart)
            file.writeText(setPrettyString(Gson().toJson(blockModels)))
        }
        blocks.forEach { item ->
            val materialName = BlockParser.getMaterialByData(item.block?.data!!).name.lowercase()
            val path =
                "${item.namespace}:" + if (item.modelPath != null) "${item.modelPath}" else "auto_generated/${item.id}"
            val multipart = Multipart(
                BlockParser.getFacingByData(item.block.data),
                Apply(path)
            )
            val file = File(getMinecraftAssetsPath() + sep + "blockstates" + sep + materialName + ".json")
            if (!file.exists())
                file.createNewFile()
            var blockModels = Gson().fromJson(file.readText(), BlockModel::class.java)
            if (blockModels == null)
                blockModels = BlockModel()
            blockModels.multipart.add(multipart)
            file.writeText(setPrettyString(Gson().toJson(blockModels)))


        }

    }

    private fun deleteFilesInFolder(folder: String) =
        File(folder).listFiles()?.forEach { it.delete() }

    private fun deleteFolder(folder: String) = File(folder).delete()
    private fun createDirectory(folder: String) = File(folder).mkdirs()
    private fun recreateDirectory(path: String){
        deleteFilesInFolder(path)
        deleteFolder(path)
        createDirectory(path)
    }
    private fun createFile(file: String) = File(file).createNewFile()

    init {
        deleteFilesInFolder(getMinecraftAssetsPath() + sep + "blockstates")

        mkdirs()
        initBaseDirectory()
        generateFont()
        generateSounds()
        generateItems()
        sortItems()
        generateBlocks()
    }

    companion object {
        val sep: String
            get() = File.separator

        fun generate() = ResourcePack()
        private fun setPrettyString(line: String): String =
            GsonBuilder().setPrettyPrinting().create().toJson(JsonParser().parse(line).asJsonObject)
                .replace("\\\\", "\\")

        private fun getAutoGeneratedPath(namespace: String = "minecraft") =
            getAssetsFolder() + File.separator + namespace + File.separator + "models" + File.separator + "auto_generated"

        /**
         * Возвращает путь <plugin>/pack/assets
         */
        private fun getAssetsFolder() =
            AstraLibs.instance.dataFolder.toString() +
                    File.separator + "pack" +
                    File.separator + "assets"

        /**
         * Возвращает путь AstraItems/pack/assets/minecraft
         */
        private fun getMinecraftAssetsPath(): String = getAssetsFolder() +
                File.separator + "minecraft"

        /**
         * Возвращает путь AstraItems/pack/assets/minecraft/models
         */
        private fun getMinecraftModelsPath(): String = getMinecraftAssetsPath() +
                File.separator + "models"

        /**
         * Возвращает путь AstraItems/pack/assets/minecraft/models/item
         */
        private fun getMinecraftItemsModelsPath(): String = getMinecraftModelsPath() + File.separator + "item"

        /**
         * Возвращает путь AstraItems/pack/assets/minecraft/block
         */
        private fun getMinecraftBlocksModelsPath(): String = getMinecraftModelsPath() + File.separator + "block"

        /**
         * Возвращает путь AstraItems/pack/assets/minecraft/font
         */
        private fun getFontPath() = getMinecraftAssetsPath() + File.separator + "font"

        private fun saveFileFromResources(path: String, outputPath: String, replace: Boolean = true): Boolean {
            val option = if (replace) StandardCopyOption.REPLACE_EXISTING else null
            val input = AstraLibs.instance.getResource(path)
            val output = File(outputPath).apply { createNewFile() }
            Files.copy(
                input ?: return false,
                output.toPath(),
                option
            )
            return true
        }

    }
}