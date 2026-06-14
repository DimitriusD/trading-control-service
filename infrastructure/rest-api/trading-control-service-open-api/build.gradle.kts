plugins {
    `java-library`
    `maven-publish`
}

group = "com.trading.contracts"
version = rootProject.version.toString()

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }

    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            groupId = "com.trading.contracts"
            artifactId = "trading-control-service-openapi"
            version = project.version.toString()

            pom {
                name.set("Trading Control Service OpenAPI Contract")
                description.set("OpenAPI YAML contract for trading-control-service")
            }
        }
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
}
