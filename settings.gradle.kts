@file:Suppress("UnstableApiUsage")

rootProject.name = "new-naver-map-compose"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

pluginManagement {
  repositories {
    gradlePluginPortal()
    google {
      content {
        includeGroupByRegex(".*google.*")
        includeGroupByRegex(".*android.*")
      }
    }
    mavenCentral()
  }
}

buildCache {
  local {
    removeUnusedEntriesAfterDays = 7
  }
}

include(
  ":sample",
  ":navermap-runtime",
  ":navermap-ui",
  ":navermap-ui-semantics",
  ":navermap-ui-modifier",
  ":navermap-ui-codegen",
  ":navermap-test",
)
