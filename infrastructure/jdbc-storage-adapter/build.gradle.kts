plugins {
    `java-library`
}

dependencies {
    implementation(platform(libs.springBom))
    implementation(project(":application"))

    implementation("org.springframework:spring-context")
    implementation("org.springframework.boot:spring-boot")
    implementation(libs.springBootStarterJdbc)
    implementation(libs.springBootStarterDataJdbc)
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")

    runtimeOnly(libs.postgresql)

    compileOnly(libs.lombok)

    annotationProcessor(libs.lombok)

    testImplementation(libs.springBootStarterTest)
    testRuntimeOnly(libs.junitPlatformLauncher)
}
