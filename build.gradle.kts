// Top-level build file where you can add configuration options common to all sub-projects/modules.
//buildscript {
//    ext {
//        compose_version = '1.5.1'
//        hilt_version = '2.48.1'
//        kotlin_version = '1.9.0'
//    }
//    dependencies {
//        classpath "com.google.dagger:hilt-android-gradle-plugin:$hilt_version"
//    }
//}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
}
