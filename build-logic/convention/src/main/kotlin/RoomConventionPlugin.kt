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

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

import androidx.room.gradle.RoomExtension
import com.google.devtools.ksp.gradle.KspExtension

class RoomConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            with(pluginManager) {
                apply("androidx.room"
                apply("com.google.devtools.ksp")
            }
            
            extensions.configure<KspExtension> {
                if (isRunningOnAndroidDevice) {
                    // Optimierungen für Mobile
                    arg("room.verifySchema", "false")
                    arg("room.generateKotlin", "false") // Java-Generierung ist oft "leichter" für mobile CPUs
                    println("Room: Mobile-Optimierung aktiv (Schema-Check aus, Java-Gen an)")
                } else {
                    // Volle Power für den PC
                    arg("room.verifySchema", "true")
                    arg("room.generateKotlin", "true") 
                    println("Room: Desktop-Konfiguration aktiv (Vollständige Prüfung)")
                }
            }

            extensions.configure<RoomExtension> {
                // The schemas directory contains a schema file for each version of the Room database.
                // This is required to enable Room auto migrations.
                // See https://developer.android.com/reference/kotlin/androidx/room/AutoMigration.
                schemaDirectory("$projectDir/schemas")
            }

            dependencies {
                "implementation"(libs.findLibrary("room.runtime").get())
                "implementation"(libs.findLibrary("room.ktx").get())
                "ksp"(libs.findLibrary("room.compiler").get())
            }
        }
    }
}