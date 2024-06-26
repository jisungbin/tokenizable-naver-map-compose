/*
 * Copyright 2024 SOUP, Ji Sungbin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@file:Suppress("UnstableApiUsage")

plugins {
  id("com.android.library")
  kotlin("android")
  id(libs.plugins.gradle.publish.maven.get().pluginId)
}

android {
  namespace = "land.sungbin.navermap.runtime"
  compileSdk = 34

  defaultConfig {
    minSdk = 21
  }

  sourceSets {
    getByName("main").java.srcDir("src/main/kotlin")
    getByName("test").java.srcDir("src/main/kotlin")
  }

  buildFeatures {
    compose = true
  }

  composeOptions {
    kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }

  kotlinOptions {
    compileOptions {
      freeCompilerArgs = freeCompilerArgs + listOf(
        "-opt-in=land.sungbin.navermap.runtime.InternalNaverMapRuntimeApi",
        "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
        "-opt-in=androidx.compose.runtime.InternalComposeApi",
      )
    }
  }

  testOptions.unitTests {
    isReturnDefaultValues = true
    isIncludeAndroidResources = true
    all { test ->
      test.useJUnitPlatform()
    }
  }
}

kotlin {
  explicitApi()
}

dependencies {
  compileOnly(libs.compose.runtime)
  compileOnly(libs.compose.uiutil)
  implementation(libs.androidx.annotation)
  implementation(libs.androidx.collection)
  implementation(libs.jetbrains.annotation)

  testImplementation(kotlin("test"))
  testImplementation(kotlin("reflect")) // optional used in assertk
  testImplementation(libs.test.assertk)
  testImplementation(libs.test.mockk)
  testImplementation(libs.test.junit5.params)
  testImplementation(libs.test.kotlin.coroutines)

  testImplementation(libs.compose.runtime)
  testImplementation(libs.compose.uiutil)
}
