import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginExtension

plugins {
    alias(libs.plugins.openapiGenerator)
}

group = "com.trading"
version = "0.1.3-SNAPSHOT"

allprojects {
    repositories {
        mavenLocal()
        mavenCentral()
    }
}

subprojects {
    plugins.withType<JavaPlugin> {
        extensions.configure<JavaPluginExtension> {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(21))
            }
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

tasks.wrapper {
    gradleVersion = "9.2.1"
    distributionType = Wrapper.DistributionType.BIN
}

tasks.register("collectSources") {
    group = "documentation"
    description = "Збирає весь вихідний код проєкту в один текстовий документ (build/all-code.txt)"

    val rootPath = rootDir
    val sources = fileTree(rootPath) {
        include(
            "**/*.java",
            "**/*.kts",
            "**/*.sql",
            "**/*.yaml",
            "**/*.yml",
            "**/*.properties",
            "**/*.toml",
        )
        exclude(
            "**/build/**",
            "**/.gradle/**",
            "**/.git/**",
            "**/.idea/**",
            "**/generated/**",
            "**/node_modules/**",
        )
    }
    inputs.files(sources)

    val outputFile = layout.buildDirectory.file("all-code.txt")
    outputs.file(outputFile)

    doLast {
        val target = outputFile.get().asFile
        target.parentFile.mkdirs()
        val files = sources.files.sortedBy { it.absolutePath }
        target.bufferedWriter().use { writer ->
            files.forEach { file ->
                val relative = file.relativeTo(rootPath).invariantSeparatorsPath
                writer.write("// ===================================================================\n")
                writer.write("// FILE: $relative\n")
                writer.write("// ===================================================================\n\n")
                writer.write(file.readText())
                writer.write("\n\n")
            }
        }
        logger.lifecycle("Зібрано ${files.size} файлів у ${target.absolutePath}")
    }
}
