plugins {
    alias(libs.plugins.nordic.application.compose)
    alias(libs.plugins.nordic.hilt)
}

group = "no.nordicsemi.android.kotlin.ble"

android {
    namespace = "no.nordicsemi.android.kotlin.ble.app.client"
}

dependencies {
//    implementation(project(":advertiser"))
//    implementation(project(":scanner"))
//    implementation(project(":client"))
    // scanner用本地的，做好国际化，可能还有一些定制需求
    implementation(project(":uiscanner"))
//    implementation(project(":server"))
    implementation(libs.nordic.blek.advertiser)
    implementation(libs.nordic.blek.scanner)
    implementation(libs.nordic.blek.client)
    implementation(libs.nordic.blek.server)
//    implementation(libs.nordic.blek.uiscanner)


    implementation(libs.nordic.theme)
    implementation(libs.nordic.navigation)
    implementation(libs.nordic.permissions.ble)
    implementation(libs.nordic.logger)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.compose.material.iconsExtended)
    implementation(libs.androidx.lifecycle.runtime.compose)
}
