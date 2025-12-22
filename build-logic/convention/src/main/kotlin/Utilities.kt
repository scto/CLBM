/*
 * Copyright 2024 Thomas Schmidl
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
 
import org.gradle.api.Project
import com.google.devtools.ksp.gradle.KspExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.api.tasks.compile.JavaCompile
import com.android.build.api.variant.AndroidComponentsExtension

/**
 * Erkennt, ob der Build auf einem Android-Gerät (RV2IDE, Termux) läuft.
 */
val Project.isRunningOnAndroidDevice: Boolean
    get() {
        val osName = System.getProperty("os.name")?.lowercase() ?: ""
        val osArch = System.getProperty("os.arch")?.lowercase() ?: ""
        return osName.contains("android") || osArch.contains("aarch64")
    }

/**
 * Holt die Android API Version des Handys, auf dem gerade gebuildet wird.
 * Standardmäßig 24 (Android 7), falls nicht erkennbar.
 */
val Project.deviceApiLevel: Int
    get() = if (isRunningOnAndroidDevice) {
        // Unter Android liefert diese Property die API Version (z.B. "33")
        System.getProperty("ro.build.version.sdk")?.toIntOrNull() 
            ?: android.os.Build.VERSION.SDK_INT // Alternativer Zugriff
    } else {
        24 // Default für PC/CI
    }

/**
 * Zentrale Konfiguration für Room & KSP Performance
 */
fun Project.configureRoomKsp() {
    extensions.configure<KspExtension>("ksp") {
        if (isRunningOnAndroidDevice) {
            arg("room.verifySchema", "false") // Verhindert SQLite Native Error
            arg("room.generateKotlin", "false") // Java-Gen ist schneller auf Mobile
        } else {
            arg("room.verifySchema", "true")
            arg("room.generateKotlin", "true")
        }
    }
}

/**
 * Optimiert die Compiler-Performance für mobile CPUs
 */
fun Project.configureCommonPerformance() {
    val isMobile = isRunningOnAndroidDevice

    // Kotlin-Compiler Threads begrenzen
    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            if (isMobile) {
                freeCompilerArgs = freeCompilerArgs + listOf("-Xbackend-threads=2")
            }
        }
    }

    // Java-Compiler Speicher begrenzen
    tasks.withType<JavaCompile>().configureEach {
        if (isMobile) {
            options.forkOptions.memoryMaximumSize = "512m"
        }
    }
}
