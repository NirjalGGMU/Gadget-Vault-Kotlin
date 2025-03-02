plugins {
    id("com.android.application") version "8.7.3" apply false
    id("org.jetbrains.kotlin.android") version "1.9.24" apply false
    alias(libs.plugins.google.gms.google.services) apply false
}

tasks.register<Delete>("clean") {
    delete(layout.buildDirectory)
}