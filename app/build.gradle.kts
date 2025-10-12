import org.gradle.kotlin.dsl.androidTestImplementation
import org.gradle.kotlin.dsl.implementation
import org.gradle.kotlin.dsl.testImplementation

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    id("com.google.devtools.ksp") version "2.0.21-1.0.26"
}

hilt {
    enableAggregatingTask = false
}

android {
    namespace = "com.atri.seduley"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.atri.seduley"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // ===== 签名配置（release 可选）=====
    signingConfigs {
        create("release") {
            val keystorePath = project.findProperty("KEYSTORE_FILE")?.toString()
            if (!keystorePath.isNullOrEmpty()) {
                storeFile = file(keystorePath)
                storePassword = project.findProperty("KEYSTORE_PASSWORD") as String?
                keyAlias = project.findProperty("KEY_ALIAS") as String?
                keyPassword = project.findProperty("KEY_PASSWORD") as String?
            } else {
                println("未检测到 keystore 配置, 将使用默认 debug 签名")
            }
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles.clear()
            signingConfig = signingConfigs.findByName("release")
        }

        getByName("debug") {
            isMinifyEnabled = false
            isDebuggable = true
        }
    }

    buildFeatures {
        compose = true
        viewBinding = true
        dataBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // ============ Core / Kotlin ============
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // ============ Compose ============
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // ============ Room ============
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // ============ Dagger-Hilt ============
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose)
    ksp(libs.androidx.hilt.compiler)

    // ============ Utilities ============
    implementation(libs.protolite.well.known.types)
    implementation(libs.threetenabp)
    implementation(libs.yitter.idgenerator)
    implementation(libs.okhttp3.okhttp)
    implementation(libs.okhttp.urlconnection)
    implementation(libs.jsoup)
    implementation(libs.tensorflow.lite)
    implementation(libs.tensorflow.lite.gpu)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.security.crypto)
    implementation(libs.androidx.palette)
    implementation(libs.material.kolor)
    implementation(libs.androidx.core.splashscreen)

    // ============ UI Libraries ============
    implementation(libs.material)
    implementation(libs.ucrop.v2211)
    implementation(libs.coil.compose)
    implementation(libs.accompanist.navigation.animation)

    // ============ Testing ============
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}