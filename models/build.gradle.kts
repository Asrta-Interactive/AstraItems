plugins {
    kotlin("plugin.serialization")
    kotlin("jvm")
}

group = "com.astrainteractive.empire_items.models"
version = Dependencies.version

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    // Kotlin
    implementation(Dependencies.Libraries.kotlinGradlePlugin)
    // Serialization
    implementation(Dependencies.Libraries.kotlinxSerialization)
    implementation(Dependencies.Libraries.kotlinxSerializationJson)
    implementation(Dependencies.Libraries.kotlinxSerializationYaml)
}
