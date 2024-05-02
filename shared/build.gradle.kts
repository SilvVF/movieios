import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.serialization)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.sqlDelight)
    id("org.jetbrains.compose")
    id("com.google.devtools.ksp") version "1.9.23-1.0.20"
    id("de.jensklingenberg.ktorfit")
    alias(libs.plugins.buildkonfig)
}

val ktorfitVersion = "1.13.0"

kotlin {

    // export correct artifact to use all classes of library directly from Swift
    targets.withType(org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget::class.java).all {
        binaries.withType(org.jetbrains.kotlin.gradle.plugin.mpp.Framework::class.java).all {
            export("dev.icerock.moko:mvvm-core:0.16.1")
            export("dev.icerock.moko:mvvm-state:0.16.1")
        }
    }

    compilerOptions {
        // common compiler options applied to all Kotlin source sets
        freeCompilerArgs.add("-Xexpect-actual-classes")

    }
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        unitTestVariant.sourceSetTree.set(KotlinSourceSetTree.test)
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = false
            linkerOpts.add("-lsqlite3")


        }
    }

    sourceSets {
        commonMain.dependencies {
            api("dev.icerock.moko:mvvm-core:0.16.1") // only ViewModel, EventsDispatcher, Dispatchers.UI
            api("dev.icerock.moko:mvvm-flow:0.16.1") // api mvvm-core, CFlow for native and binding extensions
            api("dev.icerock.moko:mvvm-state:0.16.1") // api mvvm-livedata, ResourceState class and extensions
            api("dev.icerock.moko:mvvm-flow-resources:0.16.1") // api mvvm-core, moko-resources, extensions for Flow with moko-resources

            // compose multiplatform
            api("dev.icerock.moko:mvvm-compose:0.16.1") // api mvvm-core, getViewModel for Compose Multiplatform
            api("dev.icerock.moko:mvvm-flow-compose:0.16.1") // api mvvm-flow, binding extensions for Compose Multiplatform

            implementation("org.jetbrains.compose.runtime:runtime:1.6.10-beta03")
            implementation("org.jetbrains.compose.foundation:foundation:1.6.10-beta03")

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.contentNegotiation)
            implementation(libs.ktor.client.serialization)
            implementation(libs.sqldelight.coroutines)
            implementation(libs.sqldelight.runtime)

            implementation(libs.paging.common)
            implementation(libs.paging.compose.common)
            implementation(libs.paging.testing)

            implementation(libs.androidx.paging3.extensions)

            implementation(libs.koin.core)

            implementation("co.touchlab:kermit:2.0.3")
            implementation("de.jensklingenberg.ktorfit:ktorfit-lib:$ktorfitVersion")

            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1-Beta")
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")

            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

            implementation("co.touchlab:stately-iso-collections:2.0.7")
            implementation("co.touchlab:stately-concurrency:2.0.7")
            implementation("co.touchlab:stately-isolate:2.0.7")
            implementation("co.touchlab:stately-concurrent-collections:2.0.7")
            implementation("co.touchlab:stately-common:2.0.7")

            api("com.arkivanov.decompose:decompose:3.0.0")
            api("com.arkivanov.decompose:extensions-compose:3.0.0")
        }
        commonTest.dependencies {
            implementation("dev.icerock.moko:mvvm-test:0.16.1")
        }
        androidMain.dependencies {
            implementation(libs.sqldelight.driver.android)
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.androidx.core)
        }
        iosMain.dependencies {
            implementation(libs.sqldelight.driver.ios)
            implementation(libs.sqliter)
            implementation(libs.ktor.client.ios)
            implementation(libs.paging.runtime.uikit)
        }
    }
}


buildConfig {
    val properties = Properties().also {
        it.load(project.rootProject.file("local.properties").inputStream())
    }
    // default config is required
    buildConfigField("TMDB_API_KEY", properties.getProperty("TMDB_API_KEY"))
    buildConfigField("TMDB_ACCESS_TOKEN", properties.getProperty("TMDB_ACCESS_TOKEN"))
    buildConfigField("SUPABASE_URL", properties.getProperty("SUPABASE_URL"))
    buildConfigField("SUPABSE_ANON_KEY", properties.getProperty("SUPABSE_ANON_KEY"))
    buildConfigField("GOOGLE_WEB_CLIENT_ID", properties.getProperty("GOOGLE_WEB_CLIENT_ID"))
}

dependencies {
    with("de.jensklingenberg.ktorfit:ktorfit-ksp:$ktorfitVersion") {
        add("kspCommonMainMetadata", this)
        add("kspAndroid", this)
        add("kspAndroidTest", this)
        add("kspIosX64", this)
        add("kspIosX64Test", this)
        add("kspIosArm64", this)
        add("kspIosArm64Test", this)
        add("kspIosSimulatorArm64", this)
        add("kspIosSimulatorArm64Test", this)
    }
}

sqldelight {
    databases.create("Database") {
        packageName.set("io.silv")
    }
}

android {
    namespace = "io.silv.moviemp"
    compileSdk = 34
    defaultConfig {
        minSdk = 26
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
