plugins {
    id("com.android.application") version "9.4.1" apply false
}


// Windows Java launchers may decode UTF-8 Gradle argument files with the system
// code page. Keep generated classpaths ASCII when a checkout has non-ASCII text.
if (System.getProperty("os.name").startsWith("Windows") &&
    rootDir.absolutePath.any { it.code > 127 }) {
    val checkoutId = rootDir.absolutePath.hashCode().toUInt().toString(16)
    val outputRoot = File(gradle.gradleUserHomeDir, "practicalfive-build/$checkoutId")
    allprojects {
        layout.buildDirectory.set(File(outputRoot, name))
    }
}
