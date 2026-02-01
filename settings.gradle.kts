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
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
        // Flutter AAR from arr path (build with: cd arr && flutter build aar)
        maven {
            // Priority:
            // 1) CI env var (Jenkins)
            // 2) local dev override (optional)
            // 3) default relative path in a multi-repo workspace
            val repoPath =
                System.getenv("MOVIE_CORE_MAVEN_REPO")
                    ?: System.getProperty("movieCoreMavenRepo")
                    ?: "${rootDir}/../movie_core/build/host/outputs/repo"

            url = uri(repoPath)
        }
        maven {
            val storageUrl = System.getenv("FLUTTER_STORAGE_BASE_URL") ?: "https://storage.googleapis.com"
            url = uri("$storageUrl/download.flutter.io")
        }
    }
}

rootProject.name = "MovieAndroid"
include(":app")
 