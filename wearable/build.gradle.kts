plugins {
   alias(libs.plugins.customAndroidApplication)
}

dependencies {
    //  Include Samsung Health Sensor SDK
    implementation(fileTree("libs"))

    // Wear Compose
    implementation(libs.bundles.compose.wear)

    // Play Services for Wearable
    implementation(libs.google.play.services.wearable)

    // Gson: Converter for Json to Variable
    implementation(libs.gson)

    // Datastore
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore.preferences.core)

    implementation("com.google.firebase:firebase-crashlytics-buildtools:2.9.9")
}