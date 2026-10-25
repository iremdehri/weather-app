import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.kampplus.hava"
    // compileSdk: derlemede erisilebilen Android API seviyesi.
    compileSdk = 36

    buildFeatures{
        buildConfig=true
        compose=true
    }

    defaultConfig {
        applicationId = "com.kampplus.hava"
        // minSdk: uygulamanin calisacagi en dusuk Android surumu.
        minSdk = 26
        // targetSdk: davranislarin hangi surume gore ayarlanacagi.
        targetSdk = 36
        // versionCode sayisal surum kodu, versionName kullaniciya gosterilen surum.
        versionCode = 1
        versionName = "0.1.0"

        val properties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            properties.load(localPropertiesFile.inputStream())
        }

        val groqKey = properties.getProperty("GROQ_API_KEY") ?: ""
        buildConfigField("String", "GROQ_API_KEY", "\"$groqKey\"")

        val weatherKey = properties.getProperty("WEATHER_API_KEY") ?: ""
        buildConfigField("String", "WEATHER_API_KEY", "\"$weatherKey\"")

    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    implementation("androidx.compose.material:material-icons-extended:1.6.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.3")
    implementation("androidx.navigation:navigation-compose:2.7.7")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation("io.coil-kt:coil-gif:2.6.0")

    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")

    testImplementation(libs.junit)

    // @Preview'lari Android Studio'da gosterebilmek icin yalnizca debug derlemesinde.
    debugImplementation(libs.androidx.compose.ui.tooling)
}
