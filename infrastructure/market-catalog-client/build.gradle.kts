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
    implementation(libs.springBootHttpClient)

    api(libs.springWeb)
    api(libs.jacksonDatabind)
    api(libs.openapiJacksonNullable)

    implementation(libs.mapstruct)
    implementation(libs.resilience4jSpringBoot4)
    implementation(libs.springBootStarterAspectj) // brings aspectjweaver so resilience4j @Retry/@CircuitBreaker aspects activate

    compileOnly(libs.jakartaAnnotationApi)
    compileOnly(libs.lombok)

    annotationProcessor(libs.lombok)
    annotationProcessor(libs.mapstructProcessor)

    testImplementation(libs.junitJupiter)
    testRuntimeOnly(libs.junitPlatformLauncher)

    openapi("com.trading.contracts:market-catalog-service-openapi:0.1.0-SNAPSHOT")
}

val extractedOpenApiDir = layout.buildDirectory.dir("openapi/contracts/market-catalog-service")

val extractMarketCatalogOpenApiContract by tasks.registering(Sync::class) {
    group = "openapi"
    description = "Extracts the market-catalog-service OpenAPI YAML contract from the published JAR"

    from(openapi.elements.map { artifacts -> artifacts.map { zipTree(it.asFile) } })
    into(extractedOpenApiDir)
}

val openApiGeneratedDir = layout.buildDirectory.dir("generated/market-catalog-client")

tasks.named<GenerateTask>("openApiGenerate") {
    dependsOn(extractMarketCatalogOpenApiContract)
    inputs.dir(extractedOpenApiDir)

    generatorName.set("java")
    library.set("restclient")
    cleanupOutput.set(true) // wipe stale generated files so removed contract schemas don't linger
    inputSpec.set(extractedOpenApiDir.map { it.file("openapi/openapi.yaml") })
    outputDir.set(openApiGeneratedDir.get().asFile.absolutePath)
    apiPackage.set("com.trading.catalog.client.api")
    modelPackage.set("com.trading.catalog.client.model")
    invokerPackage.set("com.trading.catalog.client.invoker")
    modelNameSuffix.set("Dto")
    configOptions.set(
        mapOf(
            "dateLibrary" to "java8",
            "useSpringBoot4" to "true",
            "useJackson3" to "true",
            "generateJsonIncludeAnnotations" to "false",
            "generateJsonSetterNullsAnnotations" to "false",
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
