import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}
kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                // Compose
                implementation(compose.desktop.currentOs){
                    exclude("org.jetbrains.compose.material")
                }
                implementation(compose.desktop.common)
                implementation(compose.ui)
                implementation(compose.foundation)
                implementation(compose.animation)
                implementation(compose.animationGraphics)
                implementation(compose.materialIconsExtended)
                implementation("com.bybutter.compose:compose-jetbrains-expui-theme:2.0.0")
                // AstraLibs
                implementation(libs.astralibs.ktxCore)
                // Navigation
                implementation(libs.arkivanov.decompose.core)
                implementation(libs.arkivanov.decompose.compose.jetbrains)
                // Coroutines
                implementation(libs.coroutines.coreJvm)
                implementation(libs.coroutines.core)
                // Serialization
                implementation(libs.kotlin.serialization)
                implementation(libs.kotlin.serializationJson)
                implementation(libs.kotlin.serializationKaml)
                // Local
                implementation(project(":api"))
                implementation(project(":models"))
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.astrainteractive.empireitems.desktop.MainKt"
        nativeDistributions {
            modules("jdk.unsupported")
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "jvm"
            packageVersion = "1.0.0"
        }
    }
}