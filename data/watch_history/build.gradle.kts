@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.flixclusive.library)
    alias(libs.plugins.flixclusive.hilt)
}

android {
    namespace = "com.flixclusive.data.watch_history"
}

dependencies {
    api(libs.stubs.util)
    api(projects.core.locale)
    api(projects.model.database)

    implementation(projects.core.database)
    implementation(libs.mockk)
}