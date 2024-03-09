// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google() // Google의 Maven 리포지터리에서 종속성을 찾습니다.
    }
    dependencies {
        val navVersion = "2.7.7" // 사용하고자 하는 Navigation 컴포넌트의 버전을 지정합니다.
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$navVersion")
    }
}


plugins {
    id("com.android.application") version "8.1.3" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    id("androidx.navigation.safeargs.kotlin") version "2.7.7" apply false
}

