# Deepfake SDK for Android

## Overview

The Deepfake SDK for Android provides a comprehensive suite of tools for detecting deepfakes in video and audio sources. It is designed to be easy to integrate into your Android applications, offering a high-level Kotlin API that wraps a powerful, enterprise-grade, and cross-platform core. This core engine is engineered for performance, providing real-time analysis and advanced spoofing detection.

You can download a demo app showcasing the SDK's functionality [here](https://github.com/dataspike-io/Deepfake-MobileSDK-Android/releases/download/v0.0.1-rc15/deepfake-demo-app-0.0.1-rc15.apk).

## Sample Code

To help you get started, `sample` directory is included in this repository. It contains a minimal set of files demonstrating a basic integration of the SDK:

*   **`SampleAppActivity.kt`**: A simple Activity that hosts the Composable UI.
*   **`SampleAppScreen.kt`**: A Jetpack Compose screen with a video player and controls to start/stop analysis.
*   **`SampleAppViewModel.kt`**: A clean ViewModel showing the core logic for initializing the SDK, handling video playback, and receiving analysis results.
*   **`build.gradle.kts.md`**: The necessary Gradle dependencies.

You can browse the [sample directory](./sample) to see a practical, focused example of how to use the SDK in your own application.

## Features

- **Real-time Deepfake Detection**
- **Secure by Design**
- **Analysis from both local video files and WebRTC streams**
- **Hooks for analyzing both video frames and audio data**
- **Automatic Pre-processing**
- **Minimal dependencies for core functionalities like decryption**

## SDK Integration Guide

### 1. Add the Maven Repository

Add the SDK's Maven repository to your project's `settings.gradle.kts` file.

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://raw.githubusercontent.com/dataspike-io/Deepfake-MobileSDK-Android/release") }
    }
}
```

### 2. Add the SDK Dependency

Add the `deepfake-sdk` as a dependency in your app-level `build.gradle.kts` file. Replace `VERSION` with the desired SDK version.

```kotlin
dependencies {
    implementation("io.dataspike:deepfake-sdk:VERSION")
}
```

## Usage

### 1. Initialize the SDK

Before you can use the SDK, you must initialize it with a valid license key.

```kotlin
val deepfakeSDK = DeepfakeSDK.getInstance()

val success = deepfakeSDK.initialize(
    applicationContext = applicationContext,
    license = "YOUR_LICENSE_KEY",
    nativeLogLevel = 4, // Optional
    deepfakeThreshold = 0.5f, // Optional
)
```

### 2. Start an Analysis Session

Provide an `AnalysisCallback` to receive results. When you start an analysis session using a `View` or a WebRTC `VideoTrack`, the SDK prepares to process both video and audio.

For local video files, you can use a `SurfaceView` or `TextureView` to display the video and pass it to the `startAnalysis` method.

```kotlin
val analysisCallback = object : AnalysisCallback {
    override fun onFrameAnalysisResult(result: AnalysisResult, message: String?) {
        // Handle the frame analysis result
    }

    override fun onAudioAnalysisResult(result: String) {
        // Handle the audio analysis result
    }

    override fun onAnalysisError(error: String) {
        // Handle analysis errors
    }
}

// Start analysis from a View
deepfakeSDK.startAnalysis(yourView, analysisCallback)

// Or from a WebRTC VideoTrack
deepfakeSDK.startAnalysis(yourVideoTrack, analysisCallback)
```
**Note:** Running simultaneous video and audio analysis may introduce minor lags.

### 3. Start an Audio-Only Analysis Session

If you only need to analyze an audio source, you can start an audio-only session.

```kotlin
deepfakeSDK.startAudioAnalysis(analysisCallback)
```

### 4. Sending Audio Data for Analysis

To analyze an audio stream, your application must capture the audio and pass it to the SDK using the `processAudioChunk` method. This method should be called repeatedly with chunks of raw PCM audio data.

The SDK will process the audio and deliver results via the `onAudioAnalysisResult` callback you provided during `startAnalysis` or `startAudioAnalysis`.

The audio data should be a `ShortArray` containing raw PCM audio, sampled at 16kHz. You should send data in chunks of 48,000 samples (which corresponds to 3 seconds of audio). If the final audio chunk is shorter than this, it should be padded with zeros to reach the required length.

```kotlin
deepfakeSDK.processAudioChunk(yourAudioData)
```

### 5. Stop the Analysis and Shut Down

```kotlin
// Stop the current analysis
deepfakeSDK.stopAnalysis()

// Release all SDK resources
deepfakeSDK.shutdownWithTimeout()
```
