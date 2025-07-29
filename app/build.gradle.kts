plugins {
    id("com.android.application")
    id("com.google.gms.google-services") // WAJIB agar Firebase jalan
}

android {
    namespace = "com.example.kickoffbooking"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.kickoffbooking"
        minSdk = 24 // ubah ke 24 jika 34 terlalu tinggi untuk device
        targetSdk = 35
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

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // AndroidX & Material
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.activity:activity:1.8.2")

    // Firebase BoM (mengelola versi otomatis)
    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))

    // Firebase libraries
    implementation("com.google.firebase:firebase-database-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")

    // Glide (untuk menampilkan gambar)
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation(libs.firebase.database)
    implementation(libs.firebase.auth)
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    // OkHttp (untuk upload ke imgbb)
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("org.json:json:20210307")
    implementation ("com.journeyapps:zxing-android-embedded:4.3.0")


    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
