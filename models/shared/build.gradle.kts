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
}
