plugins { id("com.android.application"); id("org.jetbrains.kotlin.android") }
android {
    namespace = "mic.cast"
    compileSdk = 35
    defaultConfig { applicationId = "mic.cast"; minSdk = 26; targetSdk = 34; versionCode = 1; versionName = "1.0" }
    buildTypes { debug { isMinifyEnabled = false } }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
    kotlinOptions { jvmTarget = "17" }
}
