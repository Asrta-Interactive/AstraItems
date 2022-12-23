package com.astrainteractive.empireitems.desktop.utils

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.serializer
import ru.astrainteractive.astralibs.di.IDependency
import java.io.File
import javax.swing.SwingUtilities


inline fun <T : Any> runOnMainThreadBlocking(crossinline block: () -> T): T {
    lateinit var result: T
    SwingUtilities.invokeAndWait { result = block() }
    return result
}

fun <T> IDependency<T>.init() {
    this.value
}

@OptIn(ExperimentalSerializationApi::class)
inline fun <reified T> toClass(file: File): T? = runCatching {
    val yaml = Yaml(
        Yaml.default.serializersModule,
        configuration = Yaml.default.configuration.copy(strictMode = true)
    )
    yaml.decodeFromString(serializer(T::class.java), file.readText()) as? T
}.onFailure { it.printStackTrace() }.getOrNull()