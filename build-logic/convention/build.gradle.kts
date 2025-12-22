/*
 * Copyright 2023 Thomas Schmid
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

import org.gradle.kotlin.dsl.compileOnly
import org.gradle.kotlin.dsl.gradlePlugin
import org.gradle.kotlin.dsl.libs
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "com.scto.clbm.build.logic"

val javaVersion = libs.versions.java.get().toInt()

java {
    sourceCompatibility = JavaVersion.values()[javaVersion]
    targetCompatibility = JavaVersion.values()[javaVersion]
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.valueOf("JVM_$javaVersion"))
    }
}

dependencies {
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.room.gradlePlugin)
    compileOnly(libs.dokka.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("library") {
            id = "com.scto.clbm.library"
            implementationClass = "LibraryConventionPlugin"
        }
        register("uiLibrary") {
            id = "com.scto.clbm.ui.library"
            implementationClass = "UiLibraryConventionPlugin"
        }
        register("application") {
            id = "com.scto.clbm.application"
            implementationClass = "ApplicationConventionPlugin"
        }
        register("daggerHilt") {
            id = "com.scto.clbm.dagger.hilt"
            implementationClass = "DaggerHiltConventionPlugin"
        }
        register("firebase") {
            id = "com.scto.clbm.firebase"
            implementationClass = "FirebaseConventionPlugin"
        }
        register("dokka") {
            id = "com.scto.clbm.dokka"
            implementationClass = "DokkaConventionPlugin"
        }
        register("room") {
            id = "com.scto.clbm.room"
            implementationClass = "RoomConventionPlugin"
        }
        register("ksp") {
            id = "com.scto.clbm.ksp"
            implementationClass = "KspConventionPlugin"
        }
    }
}