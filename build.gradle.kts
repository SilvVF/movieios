plugins {
    alias(libs.plugins.kotlinMultiplatform).apply(false)
    alias(libs.plugins.androidLibrary).apply(false)
    alias(libs.plugins.sqlDelight).apply(false)
    alias(libs.plugins.jetbrains.compose).apply(false)
    alias(libs.plugins.serialization).apply(false)
    id("de.jensklingenberg.ktorfit") version "1.13.0" apply false
    alias(libs.plugins.buildkonfig).apply(false)
}
