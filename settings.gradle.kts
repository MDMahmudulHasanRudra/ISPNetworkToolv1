// Force TLS 1.2 for dependency resolution to avoid JDK 25 TLS 1.3 bugs
System.setProperty("https.protocols", "TLSv1.2")
System.setProperty("jdk.tls.client.protocols", "TLSv1.2")

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "ISP Network Tool"
include(":app")
