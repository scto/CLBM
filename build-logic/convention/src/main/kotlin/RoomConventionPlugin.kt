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
            
            // Plugins anwenden (KSP, Room)
            pluginManager.apply("com.google.devtools.ksp")
            //pluginManager.apply("androidx.room")
            
            /*
            dependencies {
                "implementation"(libs.findLibrary("room.runtime").get())
                "implementation"(libs.findLibrary("room.ktx").get())
                "ksp"(libs.findLibrary("room.compiler").get())
            }
            */
            
            // Unsere neue Logik nutzen
            configureRoomKsp()
        }
    }
}

/*
// build-logic/convention/src/main/kotlin/AndroidRoomConventionPlugin.kt
import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidRoomConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.google.devtools.ksp")

            extensions.configure<KspExtension> {
                if (isRunningOnAndroidDevice) {
                    arg("room.verifySchema", "false")
                    arg("room.generateKotlin", "false")
                } else {
                    arg("room.verifySchema", "true")
                    arg("room.generateKotlin", "true")
                }
            }
        }
    }
}
*/