plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "io.silv.moviemp.android"
    compileSdk = 34
    defaultConfig {
        applicationId = "io.silv.moviemp.android"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(projects.shared)


    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.kotlinx.datetime)
    implementation(libs.coil.compose)

    implementation(libs.bundles.androidx.compose)

    coreLibraryDesugaring(libs.android.desugar)
}