
import co.touchlab.skie.configuration.EnumInterop
import co.touchlab.skie.configuration.FlowInterop
import co.touchlab.skie.configuration.SealedInterop
import co.touchlab.skie.configuration.SuspendInterop
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree
import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.nativecoroutines)
    alias(libs.plugins.ktorfit)
    id("com.codingfeline.buildkonfig")
    id("app.cash.sqldelight") version libs.versions.sqldelight
    alias(libs.plugins.skie)
}


buildkonfig {
    val properties = Properties().also {
        it.load(project.rootProject.file("local.properties").inputStream())
    }
    packageName = "io.silv.wutnextios"
    defaultConfigs {
        buildConfigField(STRING, "TMDB_API_KEY", properties.getProperty("TMDB_API_KEY"))
        buildConfigField(STRING, "TMDB_ACCESS_TOKEN", properties.getProperty("TMDB_ACCESS_TOKEN"))
        buildConfigField(STRING, "SUPABASE_URL", properties.getProperty("SUPABASE_URL"))
        buildConfigField(STRING, "SUPABSE_ANON_KEY", properties.getProperty("SUPABSE_ANON_KEY"))
        buildConfigField(STRING, "GOOGLE_WEB_CLIENT_ID", properties.getProperty("GOOGLE_WEB_CLIENT_ID"))
    }
}

kotlin {
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
        all {
            languageSettings.optIn("kotlin.experimental.ExperimentalObjCName")
            languageSettings.optIn("kotlinx.cinterop.ExperimentalForeignApi")
        }
        commonMain.dependencies {
            api("com.rickclephas.kmm:kmm-viewmodel-core:1.0.0-ALPHA-20")

            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.coroutines.core)

            implementation(libs.jetbrains.compose.plugin)
            implementation(libs.jetbrains.compose.compiler)
            implementation(libs.jetbrains.compose.runtime)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation("app.cash.paging:paging-compose-common:3.3.0-alpha02-0.5.1")

            implementation("app.cash.paging:paging-common:3.3.0-alpha02-0.5.1")
            implementation("app.cash.sqldelight:androidx-paging3-extensions:2.0.1")

            implementation(libs.skie.annotations)

            implementation(libs.kermit)

            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutines)

            implementation(libs.ktorfit)

            implementation(libs.koin.core)

            implementation("co.touchlab:stately-iso-collections:2.0.7")
            implementation("co.touchlab:stately-concurrency:2.0.7")
            implementation("co.touchlab:stately-isolate:2.0.7")
            implementation("co.touchlab:stately-concurrent-collections:2.0.7")
            implementation("co.touchlab:stately-common:2.0.7")
        }
        androidMain.dependencies {
            implementation(libs.androidx.paging)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.sqldelight.android)
        }
        iosMain.dependencies {
            api(libs.paging.runtime.uikit)
            implementation(libs.ktor.client.darwin)
            implementation(libs.sqldelight.native)
        }
    }
}


task("testClasses") {
    return@task
}

dependencies {
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.paging.runtime.ktx)
    with("de.jensklingenberg.ktorfit:ktorfit-ksp:${libs.versions.ktorfit.get()}") {
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


skie {
    features {
        group {
            SealedInterop.Enabled(true)
            EnumInterop.Enabled(true)
            coroutinesInterop.set(true)
            SuspendInterop.Enabled(false)
            FlowInterop.Enabled(true)
        }
    }
}
