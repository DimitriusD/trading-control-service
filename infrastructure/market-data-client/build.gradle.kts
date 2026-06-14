plugins {
    `java-library`
}

dependencies {
    implementation(platform(libs.springBom))
    implementation(project(":application"))

    compileOnly(libs.lombok)

    annotationProcessor(libs.lombok)
}