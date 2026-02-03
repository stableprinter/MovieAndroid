import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
}

val localProperties = Properties().apply {
    val f = rootProject.file("local.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}

fun getConfig(key: String, default: String): String =
    (project.findProperty(key) as String?)
        ?: localProperties.getProperty(key)
        ?: default

android {
    namespace = "com.movie.android"
    compileSdk = 36

    buildFeatures {
        buildConfig = true
        resValues = true
    }

    sourceSets {
        getByName("main") {
            res.srcDir("src/ciBrand/res")
        }
    }


    defaultConfig {
        applicationId = getConfig("APP_ID", "com.movie.android")
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        resValue("string", "app_name", getConfig("APP_NAME", "Movie"))

        buildConfigField("String", "APP_NAME", "\"${getConfig("APP_NAME", "Movie Debug")}\"")

        buildConfigField("String", "IMAGE_BASE_URL", "\"${getConfig("IMAGE_BASE_URL", "https://dev-cdn.local")}\"")

        buildConfigField("String", "BASE_URL", "\"${getConfig("BASE_URL", "https://api.example.com/v1")}\"")

        ndk {
            abiFilters += listOf("armeabi-v7a", "arm64-v8a", "x86_64")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        create("profile") {
            initWith(getByName("debug"))
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

configurations {
    getByName("profileImplementation") {}
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.core.splashscreen)
    // Flutter engine from arr path (Flutter module built as AAR in arr/)
    debugImplementation("com.example.movie_core:flutter_debug:1.0")
    releaseImplementation("com.example.movie_core:flutter_release:1.0")
    add("profileImplementation", "com.example.movie_core:flutter_profile:1.0")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}