// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        mavenCentral()
        maven("https://plugins.gradle.org/m2/")
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.3.1")
        classpath("com.neenbedankt.gradle.plugins:android-apt:1.8")
        classpath("org.jacoco:org.jacoco.core:0.8.8")
        classpath("org.sonarsource.scanner.gradle:sonarqube-gradle-plugin:2.8")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.20")
        classpath("org.jetbrains.kotlin:kotlin-allopen:1.8.20")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}
