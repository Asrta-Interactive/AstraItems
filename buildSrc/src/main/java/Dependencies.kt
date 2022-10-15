import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.kotlin.dsl.PluginDependenciesSpecScope
import org.gradle.kotlin.dsl.java
import java.io.InputStream
import java.net.URI
import java.util.*

object Dependencies {
    object Kotlin {
        const val version = "1.7.0"
        const val coroutines = "1.6.3"
        const val json = "1.3.3"
        const val kaml = "0.46.0"
        const val astraLibs = "2.0.0"
    }

    object Spigot {
        const val version = "1.19.2-R0.1-SNAPSHOT"
        const val placeholderAPI = "2.11.2"
        const val protocolLib = "4.8.0"
        const val worldGuard = "7.0.7"
        const val vault = "1.7"
        const val coreProtect = "21.2"
        const val modelEngine = "R3.0.0"
        const val essentials = "2.19.5-SNAPSHOT"
        const val discordSRV = "1.25.0"
        const val luckPerms = "5.4"
    }

    object Repositories {
        val lumine = "https://mvn.lumine.io/repository/maven-public/"
        const val jitpack = "https://jitpack.io"
        const val clojars = "https://repo.clojars.org/"
        const val playpro = "https://maven.playpro.com"
        const val dv8tion = "https://m2.dv8tion.net/releases"
        const val maven2 = "https://repo1.maven.org/maven2/"
        const val enginehub = "https://maven.enginehub.org/repo/"
        const val maven2Apache = "https://repo.maven.apache.org/maven2/"
        const val essentialsx = "https://repo.essentialsx.net/snapshots/"
        const val dmulloy2 = "https://repo.dmulloy2.net/repository/public/"
        const val scarsz = "https://nexus.scarsz.me/content/groups/public/"
        const val papermc = "https://papermc.io/repo/repository/maven-public/"
        const val spigotmc = "https://hub.spigotmc.org/nexus/content/repositories/snapshots/"
        const val extendedclip = "https://repo.extendedclip.com/content/repositories/placeholderapi/"
    }

    object Implementation {
        const val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Kotlin.version}"
        const val kotlinxCoroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Dependencies.Kotlin.coroutines}"
        const val kotlinxCoroutinesCore =
            "org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:${Dependencies.Kotlin.coroutines}"
        const val kotlinxSerialization = "org.jetbrains.kotlin:kotlin-serialization:${Dependencies.Kotlin.version}"
        const val kotlinxSerializationJson =
            "org.jetbrains.kotlinx:kotlinx-serialization-json:${Dependencies.Kotlin.json}"
        const val kotlinxSerializationYaml = "com.charleskorn.kaml:kaml:${Dependencies.Kotlin.kaml}"
    }

    object CompileOnly {
        const val essentialsX = "net.essentialsx:EssentialsX:${Dependencies.Spigot.essentials}"
        const val paperMC = "io.papermc.paper:paper-api:${Dependencies.Spigot.version}"
        const val spigotApi = "org.spigotmc:spigot-api:${Dependencies.Spigot.version}"
        const val spigot = "org.spigotmc:spigot:${Dependencies.Spigot.version}"
        const val protocolLib = "com.comphenix.protocol:ProtocolLib:${Dependencies.Spigot.protocolLib}"
        const val placeholderapi = "me.clip:placeholderapi:${Dependencies.Spigot.placeholderAPI}"
        const val worldguard = "com.sk89q.worldguard:worldguard-bukkit:${Dependencies.Spigot.worldGuard}"
        const val discordsrv = "com.discordsrv:discordsrv:${Dependencies.Spigot.discordSRV}"
        const val vaultAPI = "com.github.MilkBowl:VaultAPI:${Dependencies.Spigot.vault}"
        const val coreprotect = "net.coreprotect:coreprotect:${Dependencies.Spigot.coreProtect}"
        const val modelengine = "com.ticxo.modelengine:api:${Dependencies.Spigot.modelEngine}"
    }
}
fun Project.getPluginProperties(path: String): Properties {
    val properties: Properties = Properties()
    val inputStream: InputStream = rootProject.file(path).inputStream()
    properties.load(inputStream)
    return properties
}

data class Config(
    val username: String,
    val password: String,
    val token:String
)

fun Project.getConfig(): Config {
    val properties = this.getPluginProperties("astra.properties")
    return Config(
        properties.getProperty("username") ?: "",
        properties.getProperty("password") ?: "",
        properties.getProperty("token") ?: "",
    )
}