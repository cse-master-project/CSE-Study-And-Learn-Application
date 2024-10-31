import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("androidx.navigation.safeargs.kotlin")
    id("kotlin-parcelize")
    id("kotlinx-serialization")
}

val properties: Properties = Properties().apply {
    load(project.rootProject.file("local.properties").inputStream())
}

val keystoreProperties: Properties = Properties().apply {
    load(project.rootProject.file("keystore.properties").inputStream())
}

android {
    namespace = "com.cslu.cse_study_and_learn_application"
    compileSdk = 34


    signingConfigs {
        create("release") {
            storeFile = file (keystoreProperties["storeFile"]!!)
            storePassword = keystoreProperties["storePassword"].toString()
            keyAlias = keystoreProperties["keyAlias"].toString()
            keyPassword = keystoreProperties["keyPassword"].toString()
        }
    }

    defaultConfig {
        applicationId = "com.cslu.cse_study_and_learn_application"
        minSdk = 28
        targetSdk = 34
        versionCode = 12
        versionName = "2.1"

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
    implementation("androidx.activity:activity:1.8.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2")


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

    // flex box
    implementation("com.google.android.flexbox:flexbox:3.0.0")

    // Room
    val room_version = "2.5.0"

    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    ksp("androidx.room:room-compiler:$room_version")

    // mp chart
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // markdown
    implementation("io.noties.markwon:core:4.6.2")
    implementation("io.noties.markwon:image-glide:4.6.2")
}