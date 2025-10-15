## Gradle Setup for Deepfake SDK

Here are the necessary configurations for your Gradle files to integrate the Deepfake SDK.

### 1. Add the Maven Repository

In your project's `settings.gradle.kts` file, add the following Maven repository URL. This allows Gradle to find and download the SDK.

```kotlin
// settings.gradle.kts

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // Add this line for the Deepfake SDK
        maven { url = uri("https://raw.githubusercontent.com/dataspike-io/Deepfake-MobileSDK-Android/release") }
    }
}
```

### 2. Add the SDK Dependency

In your module's `build.gradle.kts` file (e.g., `app/build.gradle.kts`), add the following implementation dependency. This includes the Deepfake SDK in your application.

```kotlin
// app/build.gradle.kts

dependencies {
    // ... other dependencies

    // Deepfake SDK
    implementation("io.dataspike:deepfake-sdk:LATEST_VERSION")

    // Required for the sample code (ExoPlayer and Compose)
    implementation("androidx.media3:media3-exoplayer:1.3.1")
    implementation("androidx.media3:media3-ui:1.3.1")
    implementation("androidx.activity:activity-compose:1.9.0")
}
```

**Note:** Remember to replace `LATEST_VERSION` with the actual version of the SDK you intend to use (e.g., `1.0.0`).
