plugins {
    kotlin("jvm")
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
    implementation(libs.astralibs.spigotGui)
    implementation(libs.astralibs.orm)
    // Test
    testImplementation(kotlin("test"))
    testImplementation(libs.orgTesting)
    // Spigot
    compileOnly(libs.paperApi)
    compileOnly(libs.spigotApi)
    compileOnly("org.spigotmc:spigot:1.19-R0.1-SNAPSHOT")
    // Local
//    implementation(project(":api"))
    implementation(project(":models:shared"))
    implementation(project(":models:bukkit"))
    implementation(project(":block-generation:core"))


}

