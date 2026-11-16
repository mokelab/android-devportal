plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.maven.publish)
}

android {
    namespace = "com.mokelab.android.devportal.quickmenu.api"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        compose = true
    }
    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

dependencies {
    api(platform(libs.androidx.compose.bom))
    // Exposed because QuickMenuItem.Content is @Composable.
    api(libs.androidx.compose.runtime)
    api(libs.androidx.activity)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "com.mokelab.android.devportal"
            artifactId = "quickmenu-api"
            version = libs.versions.devportal.get()
            afterEvaluate {
                from(components["release"])
            }
        }
    }
    repositories {
        maven {
            name = "localRepo"
            url = uri("${rootProject.rootDir.absolutePath}/docs/repo")
        }
    }
}