import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.io.File
import java.security.MessageDigest
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.parcelize)
}

val releaseSigningPropertiesFile = rootProject.file("keystore.properties")
val releaseSigningProperties = Properties().apply {
    if (releaseSigningPropertiesFile.isFile) {
        releaseSigningPropertiesFile.inputStream().use(::load)
    }
}
val requiredReleaseSigningKeys = listOf(
    "storeFile",
    "storePassword",
    "keyAlias",
    "keyPassword",
)
val missingReleaseSigningKeys = requiredReleaseSigningKeys.filter { key ->
    releaseSigningProperties.getProperty(key).isNullOrBlank()
}
val releaseSigningConfigured =
    releaseSigningPropertiesFile.isFile && missingReleaseSigningKeys.isEmpty()
val releaseStoreFile = releaseSigningProperties.getProperty("storeFile")
    ?.trim()
    ?.takeIf(String::isNotEmpty)
    ?.let(::File)
    ?.let { configuredFile ->
        if (configuredFile.isAbsolute) configuredFile else rootProject.file(configuredFile.path)
    }

val admobTestDeviceIds = providers.gradleProperty("ledgram.admob.testDeviceIds")
    .orElse(providers.environmentVariable("LEDGRAM_ADMOB_TEST_DEVICE_IDS"))
    .getOrElse("")
val admobTestDeviceIdsBuildConfigValue = admobTestDeviceIds
    .replace("\\", "\\\\")
    .replace("\"", "\\\"")

val umpDebugPropertiesFile = rootProject.file("ump-debug.properties")
val umpDebugProperties = Properties().apply {
    if (umpDebugPropertiesFile.isFile) {
        umpDebugPropertiesFile.inputStream().use(::load)
    }
}
val umpTestDeviceId = umpDebugProperties.getProperty("testDeviceId", "").trim()
val umpDebugGeography = umpDebugProperties.getProperty("geography", "DISABLED")
    .trim()
    .uppercase()
val umpResetConsentOnLaunch = umpDebugProperties
    .getProperty("resetConsentOnLaunch", "false")
    .trim()
    .toBooleanStrictOrNull()
    ?: throw GradleException("resetConsentOnLaunch in ump-debug.properties must be true or false.")
val supportedUmpDebugGeographies = setOf("DISABLED", "EEA", "US")
if (umpDebugGeography !in supportedUmpDebugGeographies) {
    throw GradleException(
        "geography in ump-debug.properties must be one of: " +
            supportedUmpDebugGeographies.joinToString(),
    )
}
if (umpDebugGeography != "DISABLED" && !umpTestDeviceId.matches(Regex("[0-9A-Fa-f]{32}"))) {
    throw GradleException(
        "A 32-character UMP test device hash is required when debug geography is enabled.",
    )
}

android {
    namespace = "com.gwolbisa.ledgram"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.gwolbisa.ledgram"
        minSdk = 24
        targetSdk = 36
        versionCode = 18
        versionName = "0.9.9"

        // Configure hashed test-device IDs outside the repository for release-equivalent testing.
        buildConfigField(
            "String",
            "ADMOB_TEST_DEVICE_IDS",
            "\"$admobTestDeviceIdsBuildConfigValue\"",
        )

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    val releaseSigningConfig = if (releaseSigningConfigured) {
        signingConfigs.create("release") {
            storeFile = releaseStoreFile
            storePassword = releaseSigningProperties.getProperty("storePassword")
            keyAlias = releaseSigningProperties.getProperty("keyAlias")
            keyPassword = releaseSigningProperties.getProperty("keyPassword")
        }
    } else {
        null
    }

    buildTypes {
        debug {
            buildConfigField("String", "UMP_TEST_DEVICE_ID", "\"$umpTestDeviceId\"")
            buildConfigField("String", "UMP_DEBUG_GEOGRAPHY", "\"$umpDebugGeography\"")
            buildConfigField(
                "boolean",
                "UMP_RESET_CONSENT_ON_LAUNCH",
                umpResetConsentOnLaunch.toString(),
            )
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = releaseSigningConfig
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
        buildConfig = true
        compose = true
    }

    packaging {
        resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
}

val releaseArtifactTaskNames = setOf(
    "assembleRelease",
    "bundleRelease",
    "packageRelease",
    "packageReleaseBundle",
    "signReleaseBundle",
)
val appProjectPath = project.path
gradle.taskGraph.whenReady {
    val releaseArtifactRequested = allTasks.any { task ->
        task.project.path == appProjectPath && task.name in releaseArtifactTaskNames
    }
    if (!releaseArtifactRequested) return@whenReady

    if (!releaseSigningPropertiesFile.isFile) {
        throw GradleException(
            "Release signing is not configured. Copy keystore.properties.example to " +
                "keystore.properties and fill all four upload-key properties.",
        )
    }
    if (missingReleaseSigningKeys.isNotEmpty()) {
        throw GradleException(
            "Release signing configuration is incomplete. Set non-empty values for: " +
                missingReleaseSigningKeys.joinToString() + ".",
        )
    }
    if (releaseStoreFile?.isFile != true) {
        throw GradleException(
            "Release signing keystore was not found. Check storeFile in keystore.properties; " +
                "relative paths are resolved from the repository root.",
        )
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

