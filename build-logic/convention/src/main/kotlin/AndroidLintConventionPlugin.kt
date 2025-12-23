/*
 * Copyright 2024 Thomas Schmid
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

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.dsl.Lint
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

class AndroidLintConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            when {
                pluginManager.hasPlugin("com.android.application") ->
                    configure<ApplicationExtension> { lint(Lint::configure) }

                pluginManager.hasPlugin("com.android.library") ->
                    configure<LibraryExtension> { lint(Lint::configure) }

                else -> {
                    apply(plugin = "com.android.lint")
                    configure<Lint>(Lint::configure)
                }
            }
        }
    }
}

private fun Lint.configure() {
    // Schreibt einen XML-Bericht (gut für CI/CD Tools oder Parser im IDE)
    xmlReport = true
    xmlOutput = file("${project.rootDir}/build/reports/lint/lint-report.xml")

    // Schreibt einen HTML-Bericht (gut für Menschen lesbar)
    htmlReport = true
    htmlOutput = file("${project.rootDir}/build/reports/lint/lint-report.html")
        
    // Schreibt einen einfachen Text-Bericht
    textReport = true
    // Ausgabe in Konsole
    // textOutput = file("stdout")
    // Ausgabe in Datei
    textOutput = file("${project.rootDir}/build/reports/lint/lint-report.txt")

    // Schreibt einen SARIF-Bericht (gut für Menschen lesbar)    
    sarifReport = true
    sarifOutput = file("${project.rootDir}/build/reports/lint/lint-report.sarif")
    
    // Wenn true, bricht der Build bei Fehlern ab
    abortOnError = false

    // Wenn true, werden Warnungen als Fehler behandelt
    warningsAsErrors = false

    // Prüft auch alle Abhängigkeiten (kann den Build verlangsamen)
    checkDependencies = true
        
    // Führt Lint checks auch bei Release Builds aus
    checkReleaseBuilds = false
        
    // --- Konfigurationsdatei einbinden ---
    // Hier verweisen wir auf die lint.xml, die wir oben erstellt haben
    lintConfig = file("${project.rootDir}/lint.xml")

    // --- Baseline (Snapshot) ---
    // Wenn eine Datei hier angegeben ist, werden alle existierenden 
    // Warnungen darin gespeichert und ignoriert. Nur NEUE Warnungen werden gemeldet.
    // Nützlich für Legacy-Code.
    // baseline = file("lint-baseline.xml")
        
    disable += "GradleDependency"
}