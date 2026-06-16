rootProject.name = "trading-control-service"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

include(":application")
include(":infrastructure:app")
include(":infrastructure:rest-api")
include(":infrastructure:rest-api:trading-control-service-open-api")
include(":infrastructure:market-data-client")
include(":infrastructure:jdbc-storage-adapter")
