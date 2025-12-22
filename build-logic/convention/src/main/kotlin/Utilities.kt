// In deiner Utilities.kt
import org.gradle.api.Project
import com.google.devtools.ksp.gradle.KspExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/**
 * Prüft, ob der Build auf einem Android-Gerät (z.B. in RV2IDE/Termux) ausgeführt wird.
 */
val Project.isRunningOnAndroidDevice: Boolean
    get() {
        val osName = System.getProperty("os.name")?.lowercase() ?: ""
        val osArch = System.getProperty("os.arch")?.lowercase() ?: ""
        return osName.contains("android") || osArch.contains("aarch64")
    }
}

fun Project.configureRoomKsp() {
    extensions.configure<KspExtension>("ksp") {
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
}

fun Project.configureJvmPerformance() {
    val isMobile = isRunningOnAndroidDevice
    
    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            // Auf dem Handy wollen wir den Compiler nicht überlasten
            freeCompilerArgs = freeCompilerArgs + if (isMobile) {
                listOf(
                    "-Xbackend-threads=2", // Begrenzt Threads (schont CPU/Hitze)
                    "-Xuse-ir"
                )
            } else {
                listOf("-Xbackend-threads=0") // Nutzt alle CPU-Kerne am PC
            }
        }
    }

    // JVM Speicher für den Fork-Prozess anpassen
    tasks.withType<JavaCompile>().configureEach {
        options.forkOptions.memoryMaximumSize = if (isMobile) "512m" else "2g"
    }
}