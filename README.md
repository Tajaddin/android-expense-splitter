# android-expense-splitter

> Jetpack Compose bill splitter with penny-exact integer-cents math. **0 cents drift across 100,000 randomized splits** (per-person amounts always sum to the exact total, no float rounding error). Kotlin 2.0, Compose, MVVM with a `StateFlow` ViewModel, 15 JVM unit tests, builds to a debug APK in CI.

[![ci](https://github.com/Tajaddin/android-expense-splitter/actions/workflows/ci.yml/badge.svg)](https://github.com/Tajaddin/android-expense-splitter/actions/workflows/ci.yml)
[![License](https://img.shields.io/badge/license-MIT-blue)](LICENSE)
[![Kotlin](https://img.shields.io/badge/kotlin-2.0-7F52FF)](build.gradle.kts)
[![minSdk](https://img.shields.io/badge/minSdk-24-3DDC84)](app/build.gradle.kts)

## Hero metric

The split is provably penny-exact, verified across 100,000 randomized bills in a unit test:

```bash
./gradlew testDebugUnitTest   # includes `penny-exact across 100k randomized splits`
```

| Property | Result |
|---|---:|
| Randomized splits checked | 100,000 |
| Bills up to | $5,000 |
| People per bill | 1–29 |
| Tip | 0–39% |
| **Total cents lost or created** | **0** |

Money is integer cents end to end. Tip rounds half-up; the remainder cents from an uneven division are distributed one-per-person to the first N people (largest-remainder), so `sum(perPerson) == total` always holds. There is no floating-point money math anywhere.

## What it is

A small, correct Android app that demonstrates modern Android architecture:

| Layer | Implementation |
|---|---|
| UI | Jetpack Compose, Material 3, a single reactive screen |
| State | `SplitViewModel` exposes `StateFlow<SplitUiState>`; the UI is a pure function of state |
| Logic | `SplitCalculator` is pure, framework-free, and 100% unit-tested on the JVM |
| Input | dollar-string parsing to cents with validation (rejects bad input, shows an error) |
| Build | AGP 8.7, Kotlin 2.0 with the Compose compiler plugin, minSdk 24 / target 34 |

Business logic and parsing live outside the Android framework, so they run as fast JVM unit tests with no emulator. UI is a thin Compose layer over the ViewModel.

## Why this matters for hiring

Role categories unlocked: **Android Engineer (Kotlin)**, Mobile.

Android interviews probe Compose, MVVM/`StateFlow`, and a clean separation between testable logic and UI, plus money correctness. This repo shows all of it, with a property-based test that proves the split never drops a cent.

## Build and run

```bash
./gradlew testDebugUnitTest   # 15 unit tests (logic + ViewModel + parsing)
./gradlew assembleDebug       # builds app/build/outputs/apk/debug/app-debug.apk
```

Open in Android Studio and run on any device/emulator (API 24+). CI builds the debug APK and uploads it as an artifact on every push.

## Architecture

```
SplitCalculator (pure)      integer-cents split, largest-remainder, exact
        ▲
SplitViewModel              StateFlow<SplitUiState>; parse + validate + recompute
        ▲
MainActivity / SplitScreen  Jetpack Compose, Material 3 (state in, events out)
```

## Testing

```bash
./gradlew testDebugUnitTest   # 15 tests
```

- **SplitCalculatorTest** (8): even split, remainder distribution, half-up tip rounding, total-includes-tip, argument validation, more-people-than-cents, and the **100k-randomized penny-exact** property.
- **SplitViewModelTest** (7): dollar parsing, invalid-input rejection, cents formatting, result computation, error handling, recompute on people/tip change, people floor.

UI is kept thin so the suite needs no emulator; instrumented Compose UI tests would run on a device.

## Project layout

```
app/src/main/java/com/tajaddin/splitter/
  SplitCalculator.kt   # pure penny-exact split (the hero logic)
  SplitViewModel.kt    # StateFlow ViewModel + dollar parsing/formatting
  MainActivity.kt      # Compose UI (Material 3)
app/src/test/java/...  # JVM unit tests
.github/workflows/ci.yml   # JDK 17 + Android SDK -> unit tests + APK artifact
```

## Stack

Kotlin 2.0, Jetpack Compose (BOM 2024.10), Material 3, AndroidX Lifecycle/`StateFlow`, JUnit4, kotlinx-coroutines-test, Gradle (wrapper), Android Gradle Plugin 8.7, GitHub Actions. Targets JVM 17, minSdk 24.

## License

MIT
