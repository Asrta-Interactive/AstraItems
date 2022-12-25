plugins {
    kotlin("plugin.serialization")
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
    id("basic-plugin")
    id("basic-shadow")
    id("basic-resource-processor")
}

dependencies {
    // Kotlin
    implementation(libs.kotlinGradlePlugin)
    // Coroutines
    implementation(libs.coroutines.coreJvm)
    implementation(libs.coroutines.core)
    // Serialization
    implementation(libs.kotlin.serialization)
    implementation(libs.kotlin.serializationJson)
    implementation(libs.kotlin.serializationKaml)
    // AstraLibs
    implementation(libs.astralibs.ktxCore)
    implementation(libs.astralibs.spigotCore)
    // Test
    testImplementation(kotlin("test"))
    testImplementation(libs.orgTesting)
    // Spigot dependencies
    compileOnly(libs.essentialsx)
    compileOnly(libs.paperApi)
    compileOnly(libs.spigotApi)
    compileOnly(libs.spigot)
    compileOnly(libs.protocollib)
    compileOnly(libs.placeholderapi)
    compileOnly(libs.worldguard.bukkit)
    compileOnly(libs.discordsrv)
    compileOnly(libs.vaultapi)
    compileOnly(libs.coreprotect)
    // Local
    implementation(project(":enchantements"))
    implementation(project(":modelengine"))
    implementation(project(":api"))
    implementation(project(":models"))
    implementation(project(":block-generation:1-19-2"))
    implementation(project(":block-generation:1-19-3"))
    implementation(project(":block-generation:core"))
}

