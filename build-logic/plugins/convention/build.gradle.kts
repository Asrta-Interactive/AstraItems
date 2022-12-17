plugins {
    java
    `maven-publish`
    `java-library`
    `kotlin-dsl`
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.shadow) apply false
}
dependencies {
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
    implementation(libs.kotlinGradlePlugin)
    implementation(libs.kotlin.serialization)
    implementation("gradle.plugin.com.github.johnrengelman", "shadow", "7.1.2")
}
