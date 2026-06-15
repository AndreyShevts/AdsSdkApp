pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()

        maven {
            url = uri("https://artifactory.appodeal.com/appodeal")
        }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        google()
        mavenCentral()

        maven {
            url = uri("https://artifactory.appodeal.com/appodeal")
        }
    }
}

rootProject.name = "Ads_sdk_app"
include(":app")