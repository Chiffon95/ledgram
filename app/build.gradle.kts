import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.security.MessageDigest

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "com.gwolbisa.ledgram"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.gwolbisa.ledgram"
        minSdk = 24
        targetSdk = 36
        versionCode = 13
        versionName = "0.9.4"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }

    packaging {
        resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.google.play.services.ads)
    implementation(libs.google.user.messaging.platform)
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    debugImplementation(libs.androidx.compose.ui.tooling)

    testImplementation(libs.junit)
}

val verifyGalmuriFont = tasks.register("verifyGalmuriFont") {
    val fontFile = layout.projectDirectory.file("src/main/res/font/galmuri11.ttf")
    inputs.file(fontFile)
    doLast {
        val expectedSha256 = "e24256f42e43713d2ea086a1e1669d78b968f5b3cc547e5c157f0606ffa5def1"
        val digest = MessageDigest.getInstance("SHA-256")
            .digest(fontFile.asFile.readBytes())
            .joinToString("") { byte -> "%02x".format(byte.toInt() and 0xFF) }
        check(digest == expectedSha256) { "Bundled Galmuri11 v2.40.4 font checksum mismatch" }
    }
}

tasks.named("preBuild").configure {
    dependsOn(verifyGalmuriFont)
}

