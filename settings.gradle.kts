pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("${rootProject.projectDir.absolutePath}/repo")
        }
    }
}

rootProject.name = "DevPortal"
include(":demo")
include(":lib:devportal")
include(":lib:api")
include(":lib:logcat:core")
include(":lib:bom")
include(":lib:logcat:api")
include(":lib:logcat:basic")
