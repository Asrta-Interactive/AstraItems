
import java.util.Properties
import java.io.FileInputStream
var astraPropsFile = file("astra.properties")
if (!astraPropsFile.exists())
    astraPropsFile.createNewFile()
var astraProps = Properties().apply { load(FileInputStream(astraPropsFile)) }
val gprUser = astraProps.getProperty("gpr.user")
val gprPassword = astraProps.getProperty("gpr.password")
if (gprUser == null || gprPassword == null) {
    if (gprUser == null)
        astraProps.setProperty("gpr.user", "SET_GPR_USERNAME_HERE")
    if (gprPassword == null)
        astraProps.setProperty("gpr.password", "SET_GPR_KEY_HERE")
    astraProps.store(astraPropsFile.outputStream(), "")
    throw GradleException("You need to set your GPR keys")
}
var versionPropsFile = file("version.properties")
if (!versionPropsFile.exists())
    versionPropsFile.createNewFile()
var versionProps = Properties().apply { load(FileInputStream(versionPropsFile)) }
var versionBuildRelease = (versionProps.getProperty("VERSION_BUILD_RELEASE","0").toIntOrNull())?:0
versionProps.setProperty("VERSION_BUILD_RELEASE","${++versionBuildRelease}")
versionProps.store(versionPropsFile.outputStream(),"")
group = "com.astrainteractive"
version = "3.3.6"
val name = "EmpireItems"
description = "Custom items plugin for EmpireProjekt"
java.sourceCompatibility = JavaVersion.VERSION_16

plugins {
    java
    `maven-publish`
    `java-library`
    kotlin("jvm") version "1.5.21"
    id("com.github.johnrengelman.shadow") version "7.1.0"
}
java {
    withSourcesJar()
    withJavadocJar()
    sourceCompatibility = JavaVersion.VERSION_17
}
repositories {
    mavenLocal()
    mavenCentral()
    maven("https://repo.maven.apache.org/maven2/")
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/Astra-Interactive/AstraLibs")
        credentials {
            username = gprUser
            password = gprPassword
        }
        metadataSources {
            artifact()
        }
    }
    maven("https://repo1.maven.org/maven2/")
    maven("https://repo.dmulloy2.net/repository/public/")
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://maven.enginehub.org/repo/")
    maven("https://repo.essentialsx.net/snapshots/")
    maven("https://nexus.scarsz.me/content/groups/public/")
    maven("https://jitpack.io")
    maven("https://maven.playpro.com")
    maven {
        url = uri("https://mvn.lumine.io/repository/maven-public/")
        metadataSources {
            artifact()
        }
    }
    flatDir {
        dirs("libs")
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.21")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.5.21")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.6.1")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.5.20")
    testImplementation("junit:junit:4.13.2")
    testImplementation("com.github.seeseemelk:MockBukkit-v1.18:1.24.1")
    testImplementation("io.kotest:kotest-runner-junit5:5.2.1")
    testImplementation("io.kotest:kotest-assertions-core:5.2.1")
    testImplementation(kotlin("test"))
    compileOnly("net.essentialsx:EssentialsX:2.19.0-SNAPSHOT")
    compileOnly("io.papermc.paper:paper-api:1.18.2-R0.1-SNAPSHOT")
    compileOnly("org.spigotmc:spigot-api:1.18.2-R0.1-SNAPSHOT")
    compileOnly("org.spigotmc:spigot:1.18.2-R0.1-SNAPSHOT")
    compileOnly("com.comphenix.protocol:ProtocolLib:4.7.0")
    compileOnly("me.clip:placeholderapi:2.10.9")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.5")
    compileOnly("com.discordsrv:discordsrv:1.22.0")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("net.coreprotect:coreprotect:20.0")
    compileOnly("com.ticxo.modelengine:api:R2.5.0")
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
}



publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}
tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}



tasks {
    processResources {
        from(sourceSets.main.get().resources.srcDirs) {
            filesMatching("plugin.yml") {
                expand(
                    "name" to project.name,
                    "version" to project.version,
                    "description" to project.description
                )
            }
            duplicatesStrategy = DuplicatesStrategy.INCLUDE
        }
    }

    compileJava {
        options.encoding = "UTF-8"
    }

    shadowJar {
        dependencies {
            include(dependency(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar",".aar")))))
            include(dependency("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.21"))
            include(dependency("org.jetbrains.kotlin:kotlin-runtime:1.5.21"))
            include(dependency("org.jetbrains.kotlin:kotlin-stdlib:1.5.21"))
            include(dependency("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1"))
            include(dependency("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.6.1"))
        }
        isReproducibleFileOrder = true

        from(sourceSets.main.get().output)
        from(project.configurations.runtimeClasspath)
        manifest.attributes("Main-Class" to "com.astrainteractive.astratemplate.AstraTemplate")
        minimize()
    }

    test {
        useJUnit()
        testLogging {
            events("passed", "skipped", "failed")
            this.showStandardStreams = true
        }
    }


    register<Copy>("copyToServer") {
        val path = "D:\\Minecraft Servers\\TEST_SERVER\\plugins"
        if (path.toString().isEmpty()) {
            println("targetDir is not set in gradle properties")
            return@register
        }
        destinationDir = File(path.toString())
    }
}