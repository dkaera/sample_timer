# Pomodoro Timer

[![Android tests](https://github.com/dkaera/sample_timer/actions/workflows/tests.yml/badge.svg?branch=develop)](https://github.com/dkaera/sample_timer/actions/workflows/tests.yml?query=branch%3Adevelop)
[![Codecov](https://codecov.io/gh/dkaera/sample_timer/branch/develop/graph/badge.svg)](https://app.codecov.io/gh/dkaera/sample_timer/tree/develop)

A simple Android Pomodoro timer built with Kotlin, Jetpack Compose, and Material 3. Alternate focused work with short or long breaks using a single timer screen.

## Features

| Mode | Duration |
| --- | --- |
| Focus | 25 minutes |
| Short Break | 5 minutes |
| Long Break | 15 minutes |

- Start, pause, resume, and reset the timer.
- Switch modes to start a fresh countdown with the selected duration.
- Track remaining time with a circular progress ring that starts at 12 o’clock and sweeps clockwise.
- See a “Time’s up!” message when a session finishes.
- Light and dark themes, with dynamic colors on Android 12 and later.

Choose a mode, press **Start**, and use **Pause** whenever needed. **Reset** restores the selected mode’s full duration and stops the countdown. Select the next mode manually when a session ends.

## Getting started

### Requirements

- Android Studio with support for the project’s Android Gradle Plugin.
- JDK **25**, matching the Gradle daemon toolchain and CI configuration.
- Android SDK Platform **36** and Build Tools **36.0.0**.
- An emulator or Android device running Android **10 / API 29** or later.

The project includes the Gradle Wrapper; a separate Gradle installation is unnecessary. The first build needs internet access to download Gradle and dependencies.

### Open and run

1. Clone this repository and open its root directory in Android Studio.
2. Install the required SDK packages through **Tools → SDK Manager**.
3. Allow Gradle sync to finish.
4. Select the `app` run configuration and a device, then click **Run**.

To build a debug APK from the project root:

```sh
bash ./gradlew :app:assembleDebug
```

The APK is written to `app/build/outputs/apk/debug/app-debug.apk`.

To install it on a connected device or running emulator:

```sh
bash ./gradlew :app:installDebug
```

On Windows, use `gradlew.bat` instead of `bash ./gradlew`.

## Tests and coverage

Run all local unit tests:

```sh
bash ./gradlew :app:test
```

Run Compose UI tests with a device or emulator connected:

```sh
bash ./gradlew :app:connectedDebugAndroidTest
```

Generate JaCoCo reports for unit tests and instrumented tests:

```sh
bash ./gradlew :app:createDebugUnitTestCoverageReport
bash ./gradlew :app:createDebugAndroidTestCoverageReport
```

The instrumented coverage task also requires a connected device or emulator. Coverage reports are generated after the corresponding tests pass.

| Report | HTML entry point |
| --- | --- |
| Unit test results | `app/build/reports/tests/testDebugUnitTest/index.html` |
| Unit test coverage | `app/build/reports/coverage/test/debug/index.html` |
| Instrumented test coverage | `app/build/reports/coverage/androidTest/debug/connected/index.html` |

Unit tests cover timer state, mode changes, pausing, resuming, resetting, and completion events. Compose UI tests exercise the timer display, mode selection, controls, and completion message.

## Continuous integration

The [Android tests workflow](.github/workflows/tests.yml) runs on every push and can also be started manually from GitHub Actions. It runs two independent jobs:

- **Unit tests:** runs local tests and generates unit test coverage.
- **Instrumented tests:** runs UI tests and generates coverage on an API 35 Android emulator.

Each job uploads coverage to [Codecov](https://app.codecov.io/gh/dkaera/sample_timer) using the repository’s `CODECOV_TOKEN` secret. Reports are tagged `unit` and `instrumented`, and the badge above shows coverage for `develop`. Test reports and coverage files are also retained as workflow artifacts for 14 days.

For a fork, enable GitHub Actions, configure your own Codecov repository and `CODECOV_TOKEN` secret, and update the badge links to your repository.

## Project structure

Production code lives in `app/src/main/java/com/example/myapplication/`:

| Location | Responsibility |
| --- | --- |
| [MainActivity.kt](app/src/main/java/com/example/myapplication/MainActivity.kt) | App entry point |
| [logic/TimerMode.kt](app/src/main/java/com/example/myapplication/logic/TimerMode.kt) | Session modes and durations |
| [logic/TimerViewModel.kt](app/src/main/java/com/example/myapplication/logic/TimerViewModel.kt) | Countdown coroutine, state, and completion events |
| [ui/TimerScreen.kt](app/src/main/java/com/example/myapplication/ui/TimerScreen.kt) | Compose screen and timer controls |
| [ui/theme/](app/src/main/java/com/example/myapplication/ui/theme) | Material theme, colors, and typography |
| [app/src/test/](app/src/test) | JVM unit tests |
| [app/src/androidTest/](app/src/androidTest) | Instrumented Compose UI tests |

The screen observes `StateFlow` from `TimerViewModel` using lifecycle-aware collection. User actions flow back to the ViewModel, while completion events are exposed through `SharedFlow`.

Timer state currently lives in the ViewModel. It is not persisted across process death, and the app does not implement a foreground service or system alarm for background timing.
