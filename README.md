[한국어](README.ko.md)

# LEDgram

LEDgram is a lightweight Android LED signboard app that turns a phone screen into a customizable fullscreen message display for concerts, cheering, events, and quick visual communication.

- **App name:** LEDgram
- **Package:** `com.gwolbisa.ledgram`
- **Studio:** Gwolbisa Studio

## Project status

LEDgram is in early development. Milestone 1 provides the first working vertical slice.

Implemented:

- Single-module Android project skeleton
- Portrait Editor → landscape fullscreen Display flow
- Live Editor preview with an empty-input sample
- Static Text + Glow rendering with bold, italic, and underline styles
- Immersive Display mode, maximum window brightness, and keep-screen-on behavior
- Tap-to-show Exit overlay

Planned:

- Dot-Matrix rendering
- Marquee, blink, pulse, and fade effects
- Color selection and saved preferences
- AdMob and UMP consent support
- Firebase Analytics and Crashlytics
- Localization settings and final production branding

## Milestone 1 scope

This milestone focuses on editing a single-line message, previewing it, opening it in a landscape fullscreen display, and returning to the Editor. The visible future render/effect choices are intentionally disabled until later milestones. There is no persistence, advertising, analytics, or network access.

## Technology stack

- Kotlin
- Jetpack Compose and Material 3
- Two Android activities (`EditorActivity` and `DisplayActivity`)
- Gradle Kotlin DSL with a version catalog
- AndroidX Core SplashScreen
- minSdk 24, compileSdk 36, targetSdk 36
- JVM 17

## Project structure

```text
app/src/main/java/com/gwolbisa/ledgram/
├── model/          # Parcelable display configuration and enums
├── ui/
│   ├── components/ # Shared LED preview/text rendering
│   ├── display/    # Landscape fullscreen display
│   ├── editor/     # Portrait message editor
│   └── theme/      # Material theme and brand colors
└── util/           # Message normalization utilities
```

## Requirements

- Android Studio with Android SDK Platform 36
- JDK 17
- An Android device or emulator running Android 7.0 (API 24) or newer

## Local build

From the repository root:

```bash
./gradlew assembleDebug
./gradlew testDebugUnitTest
./gradlew lintDebug
```

The debug APK is generated under `app/build/outputs/apk/debug/` and is ignored by Git.

## Development workflow

1. Create a focused branch from `main`.
2. Keep milestone changes small and avoid adding speculative infrastructure.
3. Run the build, unit tests, and lint before requesting review.
4. Review the diff and verify the Editor → Display flow on a device or emulator.
5. Never commit local SDK paths, signing files, credentials, or generated binaries.

## Privacy and analytics

Milestone 1 requests no network permission and includes no ads, consent SDK, analytics, crash reporting, or user-data persistence. These planned integrations must receive an explicit privacy review before they are introduced.

## License

No open-source license has been selected yet. All rights are reserved unless a license is added later.

