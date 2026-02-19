# Android DevPortal

This library provides a module-style UI for Android app developers. It is useful to add development
tool to your app when development build.

# Quick Start

Add maven repository.

```gradle
repositories {
    maven {
        url "https://mokelab.github.io/android-devportal/repo"
    }
}
```

or copy `docs/repo` directory to your project and add maven repository.

```gradle
repositories {
    maven {
        url uri("path/to/repo")
    }
}
```

Add dependency. This library uses Hilt and KSP.

```gradle
plugins {
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

dependencies {
    debugImplementation("com.mokelab.android.devportal:devportal:$version")
    debugImplementation("com.mokelab.android.devportal:logcat-core:$version")
    debugImplementation("com.mokelab.android.devportal:logcat-basic:$version")
    
    // hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)    
}
```

Add Activity to your AndroidManifest.xml. It is recommended to add `src/debug/AndroidManifest.xml`
and add this Activity only for debug build.

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:tools="http://schemas.android.com/tools"
    xmlns:android="http://schemas.android.com/apk/res/android">

    <application tools:ignore="MissingApplicationIcon">
        <activity android:name="com.mokelab.android.devportal.DevPortalActivity"
            android:exported="true" android:label="DevPortal" android:taskAffinity=".devportal"
            android:theme="@style/Theme.DevPortal">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
```

Set `android:exported="true"` and `android:taskAffinity=".devportal"` to run DevPortalActivity in a
separate task. It is useful to keep DevPortalActivity alive when the main app is running.

# Usage

`devportal` module requires one or more `DevPortalFeature` to show in the UI.

Currently this library provides `LogcatFeature` to show logcat in the UI. You can also create your
own feature by implementing `DevPortalFeature` interface.

Hilt collects all `DevPortalFeature` automatically so you don't need to write Kotlin code after
adding feature module. Original Idea
is [Navigation 3 recipe Hilt modular](https://github.com/android/nav3-recipes/tree/main/app/src/main/java/com/example/nav3recipes/modular/hilt)

# How to add your own DevPortalFeature

Add Android library module to your project. Then add Compose, Hilt and KSP plugins.

```gradle
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)

    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    ///
    buildFeatures {
        compose = true
    }
}
```

Add `com.mokelab.android.devportal:api:$version` to dependencies.

```gradle
dependencies {
    implementation("com.mokelab.android.devportal:api:$version")
    
    // hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)    
}
```

Create a Hilt Module that provides `DevPortalFeature`. Add `@IntoSet` annotation to provider
function.

```kotlin
object MyRoot

@Module
@InstallIn(ActivityRetainedComponent::class)
object DevPortalMyModule {
    @IntoSet
    @Provides
    fun provideDevPortalMyFeature(
        navigator: DevPortalNavigator,
    ): DevPortalFeature {
        return object : DevPortalFeature {
            override val name: String = "My feature"
            override val installer: EntryProviderScope<Any>.() -> Unit = {
                entry<LogcatRoot> {
                    MyScreen(
                        back = {
                            navigator.goBack()
                        }
                    )
                }
            }
            override val root: Any get() = MyRoot
        }
    }
}
```

Then implement `MyScreen` Composable function.

Finally add this module to your app module dependencies.

```gradle
// app/build.gradle.kts
dependencies {
    debugImplementation("com.mokelab.android.devportal:devportal:$version")
    debugImplementation(projects.myfeature)
}
```

If your current work is done and you don't need to show this feature in DevPortal, you can remove
this module from dependencies. Hilt will automatically remove this feature from DevPortal.

# License

Apache License Version 2.0