# WiFi Guard

[![Android](https://img.shields.io/badge/platform-Android-3DDC84?logo=android&logoColor=white)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![API](https://img.shields.io/badge/min%20SDK-24-blue)](app/build.gradle.kts)
[![Target SDK](https://img.shields.io/badge/target%20SDK-36-blue)](app/build.gradle.kts)
[![License](https://img.shields.io/github/license/markjasonesguerra/wifi-guard)](LICENSE)
[![Stars](https://img.shields.io/github/stars/markjasonesguerra/wifi-guard?style=social)](https://github.com/markjasonesguerra/wifi-guard/stargazers)
[![Forks](https://img.shields.io/github/forks/markjasonesguerra/wifi-guard?style=social)](https://github.com/markjasonesguerra/wifi-guard/network/members)
[![Issues](https://img.shields.io/github/issues/markjasonesguerra/wifi-guard)](https://github.com/markjasonesguerra/wifi-guard/issues)

WiFi Guard is a lightweight Android app for monitoring Wi-Fi connectivity and real internet availability. It distinguishes between a disconnected Wi-Fi network, a connected Wi-Fi network with no internet, and a healthy connection, then records state changes in a local log.

## Features

- Wi-Fi state detection using Android network capabilities.
- Internet reachability checks using Google's `generate_204` endpoint with a socket fallback to Cloudflare DNS.
- Foreground monitoring service that keeps checking in the background.
- State-change logging to local app storage.
- Connection log screen with grouped history and clear-log support.
- Visual status states for connected, no internet, and disconnected conditions.
- Runtime notification permission handling for Android 13 and newer.

## Screens

- Main status screen with animated radar-style connection indicator.
- Logs screen with dated connection history.

Add screenshots under a `screenshots/` directory and link them here when you have app captures ready.

## Tech Stack

- Kotlin
- Android Gradle Plugin
- AndroidX AppCompat, Core KTX, Activity, ConstraintLayout
- Material Components
- Kotlin coroutines

## Requirements

- Android Studio with JDK 11 or newer.
- Android SDK for compile SDK 36.
- Android device or emulator running Android 7.0/API 24 or newer.

## Getting Started

Clone the repository:

```bash
git clone https://github.com/markjasonesguerra/wifi-guard.git
cd wifi-guard
```

Open the project in Android Studio, let Gradle sync, then run the `app` configuration on a device or emulator.

## Build

On Windows:

```powershell
.\gradlew.bat assembleDebug
```

On macOS or Linux:

```bash
./gradlew assembleDebug
```

The debug APK is generated under:

```text
app/build/outputs/apk/debug/
```

## Test

Run local unit tests:

```bash
./gradlew test
```

Run Android instrumentation tests with a connected device or emulator:

```bash
./gradlew connectedAndroidTest
```

## How It Works

`NetworkMonitorService` runs as a foreground service and checks the current active network. When Wi-Fi is active, it verifies internet access through a short HTTP 204 request and a socket fallback. The service broadcasts state changes to `MainActivity` and stores entries through `LogStore`.

The app records only changes in connection state, so the log stays focused on meaningful events instead of repeating the same status every check.

## Permissions

WiFi Guard uses these Android permissions:

- `ACCESS_NETWORK_STATE` and `ACCESS_WIFI_STATE` for connection state detection.
- `INTERNET` for reachability checks.
- `FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_DATA_SYNC`, and `FOREGROUND_SERVICE_CONNECTED_DEVICE` for background monitoring.
- `POST_NOTIFICATIONS` for Android 13+ foreground service notifications.
- `VIBRATE` is currently declared in the manifest.

## Project Structure

```text
app/src/main/java/com/example/wifiguard/
  MainActivity.kt              Main status UI and permission flow
  NetworkMonitorService.kt     Foreground connectivity monitor
  LogStore.kt                  Local connection log persistence
  LogsActivity.kt              Connection history UI

app/src/main/res/
  layout/                      Main and logs screen layouts
  drawable/                    Status backgrounds and icons
  anim/                        Radar pulse animations
```

## Contributing

Issues and pull requests are welcome. For code changes, keep the scope focused, describe the behavior change, and include tests when the change affects app logic.

## License

This project is licensed under the terms in [LICENSE](LICENSE).
