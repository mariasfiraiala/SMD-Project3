plugins {
    id("com.android.application") version "8.4.2"
}

android {
    namespace = "com.calculatoristul.keyboard"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.calculatoristul.keyboard"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            
            val serverHost = System.getenv("TELEMETRY_HOST") ?: "127.0.0.1"
            val serverPort = System.getenv("TELEMETRY_PORT") ?: "443"

            buildConfigField("String", "TELEMETRY_HOST", "\"$serverHost\"")
            buildConfigField("int", "TELEMETRY_PORT", serverPort)
        }
        
        getByName("debug") {
            // Safe configurations for local testing and debugging
            buildConfigField("String", "TELEMETRY_HOST", "\"10.0.2.2\"")
            buildConfigField("int", "TELEMETRY_PORT", "8080")
        }
    }
}

dependencies {
    // Updated to stable, secure versions
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")

    implementation(platform("com.squareup.okhttp3:okhttp-bom:5.3.2"))
    implementation("com.squareup.okhttp3:okhttp")
    
    implementation("com.squareup.okio:okio:3.16.2")
    implementation("com.google.code.gson:gson:2.11.0")

}