/*
 * Copyright 2023 Thomas Schmid 
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

// Funktion zur Erkennung der mobilen Umgebung
fun isMobileBuild(): Boolean {
    val osName = System.getProperty("os.name").lowercase()
    val userDir = System.getProperty("user.dir")
    // RV2IDE/AndroidIDE nutzen oft spezifische Pfade wie /data/data/...
    return osName.contains("android") || userDir.contains("com.tom.rv2ide")
}

ksp {
    if (isMobileBuild()) {
        // Zwingt Room, die fehlerhafte native Library zu ignorieren
        arg("room.verifySchema", "false")
        arg("room.generateKotlin", "true")
        arg("room.incremental", "false") // Nur zum Testen, ob der Fehler dann verschwindet
        println("BUILD-INFO: Mobile Umgebung erkannt. Room-Verifizierung deaktiviert.")
    } else {
        arg("room.verifySchema", "true")
        arg("room.generateKotlin", "true")
    }
}

plugins {
    alias(libs.plugins.jetpack.library)
    alias(libs.plugins.jetpack.dagger.hilt)
    alias(libs.plugins.jetpack.dokka)
    alias(libs.plugins.jetpack.room)
}

android {
    namespace = "com.scto.clbm.core.room"
}

dependencies {
    // ... Modules
    implementation(project(":core:android"))

    // ... Room
    implementation(libs.room.ktx)
    implementation(libs.room.runtime)
    ksp(libs.room.compiler)
}