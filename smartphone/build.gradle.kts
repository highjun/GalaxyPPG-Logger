plugins {
    alias(libs.plugins.customAndroidApplication)
}

android {
    namespace = "kaist.iclab.galaxyppglogger"
}

dependencies {
    // Play Services for Wearable
    implementation(libs.google.play.services.wearable)
}