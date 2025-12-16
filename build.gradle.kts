import java.util.Properties

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
}

val apiKeyProperties = Properties().apply {
    val file = rootProject.file("apiKey.properties")
    if (file.exists()) {
        load(file.inputStream())
    }
}

extra["API_KEY"] = apiKeyProperties["API_KEY"] ?: ""