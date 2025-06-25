plugins {
   alias(libs.plugins.customAndroidApplication)
}


dependencies {
    //  Include Samsung Health Sensor SDK
    implementation(fileTree("libs"))

    // Wear Compose
    implementation(libs.bundles.compose.wear)

    // Gson: Converter for Json to Variable
    implementation(libs.gson)

    // Datastore
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore.preferences.core)
}