package empirelibs

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.FileConfiguration
import java.lang.reflect.Type


class EmpireYamlParser {
    companion object {
        public fun getConfSection(cs: ConfigurationSection?): MutableMap<String, Any>? {
            cs ?: return null
            val map = mutableMapOf<String, Any>()
            for (key in cs.getKeys(false)) {
                if (cs.isConfigurationSection(key))
                    map[key] = getConfSection(cs.getConfigurationSection(key)) as Any
                else
                    map[key] = cs.get(key) as Any
            }
            return map
        }

        public fun getMap(fc: FileConfiguration?): MutableMap<String, Any>? {
            fc ?: return null
            val map = mutableMapOf<String, Any>()
            for (key in fc.getKeys(false)) {
                if (fc.isConfigurationSection(key)) {
                    map[key] = getConfSection(fc.getConfigurationSection(key)) as Any
                } else {
                    map[key] = fc.get(key) as Any
                }
            }

            return map
        }

        public fun <T> parseYamlConfig(file: FileConfiguration?, type: Type, paths: List<String>?=null): T? {
            file ?: return null
            val map = getMap(file)
            var json = JsonParser().parse(Gson().toJson(map, LinkedHashMap::class.java))
            val gson = GsonBuilder().serializeNulls().create()
            return if (paths != null) {
                for (path in paths)
                    if (json.isJsonObject)
                        json = json.asJsonObject.get(path)

                gson.fromJson(json, type)
            }
            else
                gson.fromJson(json, type)

        }

        public fun <T> parseYamlConfig(section: ConfigurationSection?, type: Type, paths: List<String>?=null): T? {
            section ?: return null
            val map = getConfSection(section)
            var json = JsonParser().parse(Gson().toJson(map, LinkedHashMap::class.java))

            val gson = GsonBuilder().serializeNulls().create()
            return if (paths != null) {
                for (path in paths)
                    if (json.isJsonObject)
                        json = json.asJsonObject.get(path)
                gson.fromJson(json, type)
            }
            else
                gson.fromJson(json, type)
        }

    }
}
