import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

plugins {
    `java-library`
    alias(libs.plugins.openapiGenerator)
}

repositories {
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/dimitriusd/trading-contracts")

        credentials {
            username = findProperty("gpr.user") as String?
                ?: System.getenv("GITHUB_ACTOR")
                ?: System.getenv("GITHUB_USERNAME")

            password = findProperty("gpr.key") as String?
                ?: System.getenv("GITHUB_TOKEN")
        }
    }
}

val openapi by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

dependencies {
    implementation(platform(libs.springBom))
    implementation(project(":application"))

    implementation("org.springframework:spring-context")
    implementation("org.springframework.boot:spring-boot")

    api(libs.springWeb)
    api(libs.jacksonDatabind)
    api(libs.jacksonJsr310)
    api(libs.openapiJacksonNullable)

    compileOnly(libs.jakartaAnnotationApi)
    compileOnly(libs.lombok)

    annotationProcessor(libs.lombok)

    openapi("com.trading.contracts:market-data-service-openapi:0.1.0-SNAPSHOT")
}

val extractedOpenApiDir = layout.buildDirectory.dir("openapi/contracts/market-data-service")

val extractMarketDataOpenApiContract by tasks.registering(Sync::class) {
    group = "openapi"
    description = "Extracts the market-data-service OpenAPI YAML contract from the published JAR"

    from(openapi.elements.map { artifacts -> artifacts.map { zipTree(it.asFile) } })
    into(extractedOpenApiDir)
}

val openApiGeneratedDir = layout.buildDirectory.dir("generated/market-data-client")

tasks.named<GenerateTask>("openApiGenerate") {
    dependsOn(extractMarketDataOpenApiContract)
    inputs.dir(extractedOpenApiDir)

    generatorName.set("java")
    library.set("restclient")
    inputSpec.set(extractedOpenApiDir.map { it.file("openapi/openapi.yaml").asFile.absolutePath })
    outputDir.set(openApiGeneratedDir.get().asFile.absolutePath)
    apiPackage.set("com.trading.mds.client.api")
    modelPackage.set("com.trading.mds.client.model")
    invokerPackage.set("com.trading.mds.client.invoker")
    modelNameSuffix.set("Dto")
    configOptions.set(
        mapOf(
            "dateLibrary" to "java8",
            "useJakartaEe" to "true",
            "openApiNullable" to "true",
            "serializationLibrary" to "jackson",
            "annotationLibrary" to "none",
            "documentationProvider" to "none",
            "hideGenerationTimestamp" to "true",
            "useBeanValidation" to "false",
            "performBeanValidation" to "false"
        )
    )
}

sourceSets {
    main {
        java {
            srcDir(openApiGeneratedDir.map { it.dir("src/main/java") })
        }
    }
}

tasks.named("compileJava") {
    dependsOn("openApiGenerate")
}
