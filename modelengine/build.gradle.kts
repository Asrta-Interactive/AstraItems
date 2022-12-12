plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

group = "com.astrainteractive.empire_items"
version = "4.2.0"


java {
    withSourcesJar()
    withJavadocJar()
    java.sourceCompatibility = JavaVersion.VERSION_1_8
    java.targetCompatibility = JavaVersion.VERSION_17
}
repositories {
    mavenLocal()
    mavenCentral()
    maven(Dependencies.Repositories.extendedclip)
    maven(Dependencies.Repositories.maven2Apache)
    maven(Dependencies.Repositories.essentialsx)
    maven(Dependencies.Repositories.enginehub)
    maven(Dependencies.Repositories.spigotmc)
    maven(Dependencies.Repositories.dmulloy2)
    maven(Dependencies.Repositories.papermc)
    maven(Dependencies.Repositories.dv8tion)
    maven(Dependencies.Repositories.playpro)
    maven(Dependencies.Repositories.jitpack)
    maven(Dependencies.Repositories.scarsz)
    maven(Dependencies.Repositories.maven2)
    modelEngige(project)
    paperMC(project)
}

dependencies {
    // Local
    implementation(project(":api"))
    implementation(project(":models"))
    // Kotlin
    implementation(Dependencies.Libraries.kotlinGradlePlugin)
    // Coroutines
    implementation(Dependencies.Libraries.kotlinxCoroutinesCoreJVM)
    implementation(Dependencies.Libraries.kotlinxCoroutinesCore)
    // Serialization
    implementation(Dependencies.Libraries.kotlinxSerialization)
    implementation(Dependencies.Libraries.kotlinxSerializationJson)
    implementation(Dependencies.Libraries.kotlinxSerializationYaml)
    // AstraLibs
    implementation(Dependencies.Libraries.astraLibsKtxCore)
    implementation(Dependencies.Libraries.astraLibsSpigotCore)
    // Test
    testImplementation(kotlin("test"))
    testImplementation(Dependencies.Libraries.orgTeting)

    // Spigot dependencies
    compileOnly(Dependencies.Libraries.essentialsX)
    compileOnly(Dependencies.Libraries.paperMC)
    compileOnly(Dependencies.Libraries.spigot)
    compileOnly(Dependencies.Libraries.spigotApi)
    compileOnly(Dependencies.Libraries.protocolLib)
    compileOnly(Dependencies.Libraries.placeholderapi)
    compileOnly(Dependencies.Libraries.worldguard)
    compileOnly(Dependencies.Libraries.discordsrv)
    compileOnly(Dependencies.Libraries.vaultAPI)
    compileOnly(Dependencies.Libraries.coreprotect)
    compileOnly(Dependencies.Libraries.modelengine)
    implementation(kotlin("script-runtime"))
}