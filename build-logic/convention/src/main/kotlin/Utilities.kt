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
 
import com.android.build.api.dsl.CommonExtension
import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val Project.isRunningOnAndroidDevice: Boolean
    get() {
        val osName = System.getProperty("os.name")?.lowercase() ?: ""
        val osArch = System.getProperty("os.arch")?.lowercase() ?: ""
        return osName.contains("android") || osArch.contains("aarch64")
    }

val Project.deviceApiLevel: Int
    get() = System.getProperty("ro.build.version.sdk")?.toIntOrNull() ?: 24

fun Project.configureCommonPerformance() {
    val isMobile = isRunningOnAndroidDevice

    // Migration von kotlinOptions zu compilerOptions
    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            if (isMobile) {
                // In der neuen API ist freeCompilerArgs eine Property vom Typ ListProperty
                // Wir fügen die Werte hinzu
                freeCompilerArgs.add("-Xbackend-threads=2")
            }
        }
    }

    tasks.withType<JavaCompile>().configureEach {
        if (isMobile) {
            options.forkOptions.memoryMaximumSize = "512m"
        }
    }
}

fun Project.configureKotlinAndroid(commonExtension: CommonExtension<*, *, *, *, *, *>) {
    val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
    
    val javaVersionStr = libs.findVersion("java").get().toString()
    val minSdkStr = libs.findVersion("minSdk").get().toString()
    val compileSdkStr = libs.findVersion("compileSdk").get().toString()

    commonExtension.apply {
        compileSdk = compileSdkStr.toInt()

        defaultConfig {
            minSdk = minSdkStr.toInt()
            
            if (isRunningOnAndroidDevice) {
                minSdk = deviceApiLevel
                resConfigs("de", "xxhdpi")
                println("Mobile-Build erkannt: API $deviceApiLevel Optimierungen aktiv.")
            }
        }

        compileOptions {
            val version = if (javaVersionStr.contains("VERSION_")) javaVersionStr else "VERSION_$javaVersionStr"
            sourceCompatibility = JavaVersion.valueOf(version)
            targetCompatibility = JavaVersion.valueOf(version)
        }
        
        // Fix für 'cruncherEnabled' Fehlermeldung
        // In neueren Versionen greift man über androidResources darauf zu
        androidResources {
            if (isRunningOnAndroidDevice) {
                @Suppress("DEPRECATION")
                (this as? com.android.build.api.dsl.AndroidResources)?.let {
                    // Falls die IDE das Interface noch alt auflöst:
                }
                // Direkter Weg für neuere AGP Versionen:
                //@Suppress("DEPRECATION")
                //commonExtension.aaptOptions.cruncherEnabled = false
            }
        }
    }
}

// In deiner build-logic/convention/src/main/kotlin/Utilities.kt
fun Project.configureRoomKsp() {
    extensions.configure<KspExtension> {
        // Wir prüfen OS und Pfad
        val isAndroidHost = System.getProperty("os.name").lowercase().contains("android")
        
        if (isAndroidHost) {
            // Dies ist der wichtigste Schalter für mobile IDEs!
            arg("room.verifySchema", "false")
            // Java-Generierung ist auf Handys oft stabiler als Kotlin-Gen
            arg("room.generateKotlin", "false")
            arg("room.incremental", "false") // Nur zum Testen, ob der Fehler dann verschwindet
            println("Room: Mobile-Optimierung aktiv.")
        } else {
            arg("room.verifySchema", "true")
            arg("room.generateKotlin", "true")
        }
    }
}