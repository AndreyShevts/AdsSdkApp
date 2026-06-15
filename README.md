# Ads SDK Demo App

## Overview

Demo Android application demonstrating Appodeal SDK integration with the following ad formats:

* Banner Ads
* Interstitial Ads
* Rewarded Video Ads
* Native Ads

Features:

* Appodeal SDK integration
* Ad event logging to Logcat and local file
* Banner session limits
* Interstitial cooldown handling
* Rewarded ad session limits
* Native ad rendering
* Portrait and landscape support

---

## Build Instructions

### Requirements

* Android Studio
* Android SDK 35
* JDK 11+

### Build Steps

1. Clone the repository.
2. Open the project in Android Studio.
3. Sync Gradle dependencies.
4. Build the project:

```bash
./gradlew assembleDebug
```

or

```text
Build → Make Project
```

5. Generated APK:

```text
app/build/outputs/apk/debug/app-debug.apk
```

---

## Testing Instructions

### Banner Ads

1. Launch the application.
2. Tap the Banner button.
3. Verify the banner is displayed.
4. Verify banner callbacks are logged.

### Interstitial Ads

1. Tap the Interstitial button.
2. Verify the ad is displayed.
3. Close the ad.
4. Verify cooldown behavior.

### Rewarded Ads

1. Tap the Rewarded button.
2. Watch the ad until completion.
3. Verify reward callback is triggered.

### Native Ads

1. Tap the Native button.
2. Verify at least 3 native ads are displayed.
3. Verify native ads render correctly.

### Orientation Testing

1. Display each ad format.
2. Rotate the device.
3. Verify rendering remains correct.

### Ad Event Logging

Ad events are logged to:

* Android Logcat
* Local application log file

Retrieve local log:

```bash
adb shell run-as com.example.ads_sdk_app cat files/appodeal_ad_events.log
```

---

## Project Structure

```text
com.example.ads_sdk_app
│
├── MainActivity.kt
│
└── ads
    ├── AdEventLogger.kt
    ├── AppodealAdsManager.kt
    └── NativeAdsAdapter.kt
```

---

## Deliverables

* Android demo application
* Appodeal SDK integration
* Ad event logging
* APK build
* Source code in a private GitHub repository
