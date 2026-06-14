import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

plugins {
    `maven-publish`
    alias(libs.plugins.openapiGenerator)
}

group = "com.trading"
version = "0.1.0-SNAPSHOT"

allprojects {
    repositories {
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

val openApiInputFile = layout.projectDirectory.file(
    "infrastructure/rest-api/src/main/resources/openapi/openapi.yaml"
)

val openApiSchemasDir = layout.projectDirectory.dir(
    "infrastructure/rest-api/src/main/resources/openapi/schemas"
)

val bundledOpenApiDir = layout.buildDirectory.dir("contracts/bundled")

val bundledOpenApiFile = layout.buildDirectory.file(
    "contracts/bundled/openapi.yaml"
)

val bundleOpenApiSpec by tasks.registering(GenerateTask::class) {
    group = "openapi"
    description = "Bundles OpenAPI spec into a single YAML contract"

    generatorName.set("openapi-yaml")
    inputSpec.set(openApiInputFile.asFile.absolutePath)
    outputDir.set(bundledOpenApiDir.get().asFile.absolutePath)

    inputs.file(openApiInputFile)
    inputs.dir(openApiSchemasDir)
    outputs.file(bundledOpenApiFile)

    configOptions.set(
        mapOf(
            "outputFile" to "openapi.yaml"
        )
    )

    doLast {
        check(bundledOpenApiFile.get().asFile.exists()) {
            "bundleOpenApiSpec: expected output not found: ${bundledOpenApiFile.get().asFile.absolutePath}"
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("openApiContract") {
            groupId = "com.trading.contracts"
            artifactId = "trading-control-service-openapi"
            version = project.version.toString()

            artifact(bundledOpenApiFile) {
                extension = "yaml"
                builtBy(bundleOpenApiSpec)
            }
        }
    }
}