plugins {
    id("java-platform")
    id("maven-publish")
}

javaPlatform {
    allowDependencies()
}

dependencies {
    constraints {
        api(libs.mokelab.devportal.api)
        api(libs.mokelab.devportal.devportal)
        api(libs.mokelab.devportal.logcat.core)
    }
}

publishing {
    publications {
        create<MavenPublication>("bom") {
            from(components["javaPlatform"])
            groupId = libs.mokelab.devportal.bom.get().group
            artifactId = libs.mokelab.devportal.bom.get().name
            version = libs.versions.devportal.get()
        }
    }
    repositories {
        maven {
            url = uri("${rootProject.rootDir.absolutePath}/repo")
        }
    }
}