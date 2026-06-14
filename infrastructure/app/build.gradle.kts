plugins {
    alias(libs.plugins.springBoot)
    alias(libs.plugins.springDependencyManagement)
    java
}

dependencies {
    implementation(project(":application"))
    implementation(project(":infrastructure:rest-api"))
    implementation(project(":infrastructure:market-data-client"))
    implementation(libs.springBootStarterWeb)
    implementation(libs.springBootStarterActuator)
    implementation(libs.springBootStarterValidation)

    compileOnly(libs.lombok)

    annotationProcessor(libs.lombok)
    annotationProcessor(libs.mapstructProcessor)

    testImplementation(libs.springBootStarterTest)

    testRuntimeOnly(libs.junitPlatformLauncher)
}
