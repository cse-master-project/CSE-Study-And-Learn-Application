import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("androidx.navigation.safeargs.kotlin")
    id("kotlin-parcelize")
    id("kotlinx-serialization")
}

val properties: Properties = Properties().apply {
    load(project.rootProject.file("local.properties").inputStream())
}

android {
    namespace = "com.example.cse_study_and_learn_application"
    compileSdk = 34

    signingConfigs {
        create("release") {
            storeFile = file("../keystore/release.keystore")
            storePassword = "rlawkdwndyddudghks"
            keyAlias = "cslu"
            keyPassword = "rlawkdwndyddudghks"
        }
    }

    defaultConfig {
        applicationId = "com.example.cse_study_and_learn_application"
        minSdk = 28
        targetSdk = 34
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
            buildConfigField("String", "server_client_id", properties.getProperty("SERVER_CLIENT_ID"))
            buildConfigField("String", "android_client_id", properties.getProperty("ANDROID_CLIENT_ID"))
            buildConfigField("String", "server_client_secret", properties.getProperty("SERVER_CLIENT_SECRET"))
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            buildConfigField("String", "server_client_id", properties.getProperty("SERVER_CLIENT_ID"))
            buildConfigField("String", "android_client_id", properties.getProperty("ANDROID_CLIENT_ID"))
            buildConfigField("String", "server_client_secret", properties.getProperty("SERVER_CLIENT_SECRET"))
            signingConfig = signingConfigs.getByName("release")
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
        viewBinding = true
        buildConfig = true
    }


}

dependencies {
    // default
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.3")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")


    // ext
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.google.android.material:material:1.11.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.google.gms:google-services:4.4.1")
    implementation("com.google.android.gms:play-services-auth:21.0.0")
    implementation("de.hdodenhof:circleimageview:3.1.0")

    // google login
    implementation("com.google.android.gms:play-services-auth:21.0.0")
    implementation("com.google.gms:google-services:4.4.1")
    implementation("androidx.credentials:credentials:1.3.0-alpha02")
//    implementation("androidx.credentials:credentials:1.2.2")
//    implementation("androidx.credentials:credentials-play-services-auth:1.2.2")
//    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.0")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.6.4")
    implementation("com.squareup.retrofit2:converter-gson:2.6.4")
    implementation("com.squareup.retrofit2:converter-scalars:2.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")

}