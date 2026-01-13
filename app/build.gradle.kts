plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-parcelize")
}

android {
    namespace = "com.publication.dealer"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.publication.dealer"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }


    buildFeatures {
        viewBinding = true
    }
    //noinspection DataBindingWithoutKapt
    android.buildFeatures.dataBinding = true
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation ("com.google.android.material:material:1.6.0")
    // Retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")

// Gson converter for Retrofit
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

// Coroutines support for Retrofit
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

// OkHttp logging (optional, useful for debugging)
    implementation ("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.11")

// Koin
    implementation("io.insert-koin:koin-core:3.2.1")
    implementation("io.insert-koin:koin-android:3.2.1")



    //Add KTX dependencies
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
//MPAndroidChart
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")

    implementation ("com.github.bumptech.glide:glide:4.16.0")

//
//    implementation ("com.tom-roush:pdfbox-android:2.0.27.0")
//    implementation ("org.apache.commons:commons-io:1.3.2")

}