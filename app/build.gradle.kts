import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)

    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")

    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")

    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")

  //  id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.example.multilocator"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.multilocator"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        // load local.properties file
        val properties = Properties()
        properties.load(project.rootProject.file("local.properties").inputStream())
        // Set API keys in BuildConfig
        buildConfigField(
            "String",
            "MAPS_API_KEY",
            "\"${properties.getProperty(" MAPS_API_KEY ")}\""
        )
    }

    buildTypes {

        android.buildFeatures.buildConfig = true
        buildFeatures {
            buildConfig = true
        }
        debug {
            isMinifyEnabled = true // optional, set to true
        }

        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

kapt {
    correctErrorTypes = true
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.lifecycle.runtime.compose.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.1.1"))

    // When using the BoM, don't specify versions in Firebase dependencies
    implementation("com.google.firebase:firebase-analytics")
    // auth
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-storage")
   // implementation("com.google.firebase:firebase-crashlytics")

    //map
    implementation("com.google.maps.android:maps-compose:4.4.1")
    implementation("com.google.maps.android:maps-compose-utils:4.3.3")
    implementation("com.google.maps.android:maps-compose-widgets:4.3.3")
    // Maps SDK for Android
    implementation ("com.google.android.gms:play-services-maps:19.0.0")
    // location
    implementation("com.google.android.gms:play-services-location:21.3.0")

    //naviagation
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // hilt
    implementation("com.google.dagger:hilt-android:2.49")
    kapt("com.google.dagger:hilt-android-compiler:2.49")
    implementation("androidx.hilt:hilt-navigation-fragment:1.2.0")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation("androidx.hilt:hilt-work:1.2.0")
    // When using Kotlin.
    kapt("androidx.hilt:hilt-compiler:1.2.0")
    // When using Java.
    annotationProcessor("androidx.hilt:hilt-compiler:1.2.0")

    // add with, this help in runtime
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    // coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.0")

    // coroutine lifeCycle scope
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.2")
    // lifeCycle
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

   // implementation ("androidx.compose.material:material-icons-extended:1.6.8")
    implementation("com.google.code.gson:gson:2.10.1")

    // Location Permission
    implementation("com.google.accompanist:accompanist-permissions:0.24.13-rc")
    implementation("com.google.android.gms:play-services-maps:18.2.0")

    implementation("androidx.compose.runtime:runtime-livedata:1.6.8")

    //coil
    implementation("io.coil-kt:coil-compose:2.6.0")

    // preferences data store
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // lottie
    implementation("com.airbnb.android:lottie-compose:6.4.0")
}