plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("basic-plugin")
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
    implementation(libs.orgTesting)
    // Spigot dependencies
    implementation(libs.essentialsx)
    implementation(libs.paperApi)
    implementation(libs.spigotApi)
    implementation(libs.spigot)
    implementation(libs.protocollib)
    implementation(libs.placeholderapi)
    implementation(libs.worldguard.bukkit)
    implementation(libs.discordsrv)
    implementation(libs.vaultapi)
    implementation(libs.coreprotect)
    // Local
    implementation(project(":api"))
    implementation(project(":models"))
}
