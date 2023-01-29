plugins {
    kotlin("plugin.serialization")
    kotlin("jvm")
    id("basic-plugin")
}
dependencies {
    // Kotlin
    implementation(libs.kotlinGradlePlugin)
    // Serialization
    implementation(libs.kotlin.serialization)
    implementation(libs.kotlin.serializationJson)
    implementation(libs.kotlin.serializationKaml)
    // AstraLibs
    implementation(libs.astralibs.ktxCore)
    implementation(libs.astralibs.spigotCore)
    // Spigot
    compileOnly(libs.paperApi)
    compileOnly(libs.spigotApi)
    compileOnly(libs.spigot)
}
