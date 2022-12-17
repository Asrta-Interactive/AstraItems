enableFeaturePreview("STABLE_CONFIGURATION_CACHE")
enableFeaturePreview("VERSION_CATALOGS")

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        maven("https://jitpack.io")
    }
}

dependencyResolutionManagement {
    repositories {
        gradlePluginPortal()
        mavenLocal()
        mavenCentral()
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
        maven("https://repo.maven.apache.org/maven2/")
        maven("https://repo.essentialsx.net/snapshots/")
        maven("https://maven.enginehub.org/repo/")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://repo.dmulloy2.net/repository/public/")
        maven("https://papermc.io/repo/repository/maven-public/")
        maven("https://m2.dv8tion.net/releases")
        maven("https://maven.playpro.com")
        maven("https://jitpack.io")
        maven("https://nexus.scarsz.me/content/groups/public/")
        maven("https://repo1.maven.org/maven2/")
        maven {
            url = uri("https://mvn.lumine.io/repository/maven-public/")
            metadataSources {
                artifact()
            }
        }
        maven {
            url = uri("https://repo.papermc.io/repository/maven-public/")
        }
    }
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }

}
rootProject.name = "build-logic"

include(
    ":plugins:convention"
)
