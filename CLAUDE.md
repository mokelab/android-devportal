# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

Android DevPortal is a library that adds a pluggable developer-tools UI (`DevPortalActivity`) to debug builds of an Android app. Features (Logcat viewer, File browser, ...) are separate modules discovered automatically through Hilt multibindings. Stack: Kotlin, Jetpack Compose (Material 3), Navigation 3, Hilt + KSP, AGP 9 / Gradle 9.1, minSdk 24.

## Commands

```sh
./gradlew :demo:assembleDebug          # build the demo app (consumes libs from docs/repo, see below)
./gradlew :demo:installDebug           # install on a connected device/emulator
./gradlew :lib:file:core:assembleRelease   # compile a single library module from source
./gradlew lint                         # Android lint (or :lib:<module>:lint)
./gradlew test                         # JVM unit tests
./gradlew :lib:api:testDebugUnitTest --tests "com.mokelab.android.devportal.api.ExampleUnitTest"   # single test
./gradlew connectedAndroidTest         # instrumented tests (device required)
```

Only the template-generated `ExampleUnitTest` / `ExampleInstrumentedTest` exist right now; there is no real test suite.

### Publishing (required to see library changes in other modules)

```sh
./gradlew :lib:file:core:publishReleasePublicationToLocalRepoRepository   # one module
./gradlew publish                                                         # every module, including the BOM
```

Both write into `docs/repo/`, which is committed and served via GitHub Pages at `https://mokelab.github.io/android-devportal/repo`.

## Modules depend on each other through `docs/repo`, not `project(...)`

This is the most important thing to know before editing. Inter-module dependencies are declared as published Maven coordinates (`libs.mokelab.devportal.*` in `gradle/libs.versions.toml`), and `settings.gradle.kts` registers `docs/repo` as a Maven repository. There are no `project(":lib:...")` dependencies anywhere.

Consequences:

- Editing source in `lib/api` (or any library) does **not** affect `demo` or dependent libraries until that module is re-published into `docs/repo`.
- Publish in dependency order when an API changes: `api` → `devportal` / `logcat:api` → `logcat:core`, `logcat:basic`, `file:core` → `bom`.
- All artifacts share one version: `devportal` in `gradle/libs.versions.toml`. Re-publishing without bumping overwrites the existing version in `docs/repo` (Gradle may serve a cached copy; use `--refresh-dependencies` if a change doesn't show up).
- Publishing rewrites `maven-metadata.xml` and its checksum files, so a large `docs/repo` diff after publishing is expected and should be committed together with the source change.
- A brand-new module can't be consumed by `demo` until it has been published once.

### Adding a new library module

1. `include(...)` it in `settings.gradle.kts`.
2. Add its coordinate to `gradle/libs.versions.toml` (`mokelab-devportal-<name>`, `version.ref = "devportal"`).
3. Copy the `publishing { ... }` block from an existing module (`release` publication, `localRepo` repository). Artifact ids flatten the path: `lib/logcat/core` → `logcat-core`.
4. Add it to the constraints in `lib/bom/build.gradle.kts` (`file-core` is currently missing from the BOM, and the BOM has only been published at 0.1.0).

## Architecture

```
lib/api          DevPortalFeature, DevPortalNavigator        (contract for feature modules)
lib/devportal    DevPortalActivity, feature list screen      (host; depends on api)
lib/logcat/api   LogcatExtension, LogcatFormat, LogcatFilter (contract for logcat extensions)
lib/logcat/core  Logcat feature                              (depends on api + logcat-api)
lib/logcat/basic Default LogcatExtension (format/filter form)
lib/file/core    File browser feature                        (depends on api)
lib/bom          java-platform BOM
demo             Sample app; pulls the libs as debugImplementation
```

### Feature plugin mechanism

- `DevPortalFeature` (`lib/api`) has three members: `name` (label in the feature list), `root` (Navigation 3 key to push when selected) and `installer` (`EntryProviderScope<Any>.() -> Unit` that registers the feature's `entry<Key> { ... }` destinations).
- Each feature module contributes itself with a Hilt `@Provides @IntoSet` function in a module installed in `ActivityRetainedComponent`. See `lib/logcat/core/.../LogcatModule.kt` and `lib/file/core/.../FileModule.kt`.
- `DevPortalActivity` injects `Set<@JvmSuppressWildcards DevPortalFeature>`, builds a single `NavDisplay`, registers its own `DevPortal` root entry and then calls every feature's `installer`. Adding a feature therefore needs no changes in `lib/devportal` — only a dependency in the consuming app.
- Navigation keys are plain objects / data classes (`LogcatRoot`, `FileRoot`, `FileFolder(name, folder)`). All features share one back stack held by `DevPortalNavigator` (`@ActivityRetainedScoped`, provided by `DevPortalModule` with `DevPortal` as the start destination); screens navigate via `navigator.goTo(key)` / `goBack()`.
- ViewModels are obtained per nav entry with `hiltViewModel()` (the Activity installs `rememberViewModelStoreNavEntryDecorator`). When a ViewModel needs the nav key's arguments, use assisted injection as in `FileListViewModel` (`@HiltViewModel(assistedFactory = ...)` + `hiltViewModel<VM, Factory>(creationCallback = ...)`).

### Logcat extension mechanism

The Logcat feature has a second plugin layer. `LogcatExtension` implementations are contributed with `@IntoSet` in `SingletonComponent`, injected into `LogcatViewModel` and sorted by `priority`. Each extension renders `SettingContent(start)` on the settings screen and receives `onStartReadLog` / `onFinishReadLog` callbacks. `logcat-core` alone shows no settings UI — `logcat-basic` supplies the default format/filter form. Logs are read by running `logcat -v <format> <filters> -d` in-process and keeping the last 200 lines.

### Host app requirements

- The consuming app must use Hilt (`@HiltAndroidApp`) and declare `DevPortalActivity` itself — the library manifest is empty. The demo does this in `demo/src/debug/AndroidManifest.xml` with `android:taskAffinity=".devportal"` so it runs as a separate launcher task.
- `Theme.DevPortal` is defined by the demo app (`demo/src/main/res/values/themes.xml`), not by the library; a consuming app has to provide the theme it references.

## Conventions

- User-visible strings in feature modules go in `res/values/strings.xml` with a Japanese translation in `res/values-ja/strings.xml`; prefix resource names with the module (e.g. `file_core_menu_title`) since library resources merge into the host app.
- Screens follow a two-layer pattern: a public composable that takes the ViewModel and collects state, delegating to a private stateless composable that takes `UiState` and lambdas (with `@Preview`s). `UiState` is a `sealed interface` nested in the ViewModel.
