// build.gradle.kts (Project level)

plugins {
    id("com.android.application") version "8.9.1" apply false
    alias(libs.plugins.google.gms.google.services) apply false


}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
