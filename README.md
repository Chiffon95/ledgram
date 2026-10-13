[한국어](README.ko.md)

# LEDgram

LEDgram is a lightweight Android LED signboard app that turns a phone screen into a customizable fullscreen message display for concerts, cheering, events, and quick visual communication.

- **App name:** LEDgram
- **Package:** `com.gwolbisa.ledgram`
- **Studio:** GWOL LABS

## Project status

LEDgram is in early development. Milestone 1 provides the first working vertical slice.

Implemented:

- Single-module Android project skeleton
- Portrait Editor → landscape fullscreen Display flow
- Fixed Editor app bar and live preview, independently scrolling controls, and a navigation/keyboard-safe Start Display action
- Animated Editor preview supporting every Motion + Visual Effect combination; empty, whitespace-only, or newline-only input shows `Hello!` as a preview-only sample while the actual normalized message remains empty and cannot be displayed fullscreen
- Text + Glow rendering with independently composited Motion (Static, R→L, L→R) and Visual Effect (None, Blink, Pulse, Fade) selectors; marquee Pulse scaling uses a direction-aware leading-edge pivot so visual effects do not alter the trajectory
- Separate Motion Speed and Effect Speed controls with selectable values from 0.25x to 5.0x; motion speed changes only marquee travel, while effect speed changes only Blink, Pulse, and Fade timing in Preview and Display
- Static display preserves an explicit line break, may auto-wrap, and fits the complete message on one or at most two centered lines without ellipsis, truncation, or clipping; marquees always render one scrolling line by showing a stored line break as a space, while switching back to Static restores the break
- Bold, italic, and underline text styles
- Basic and custom text/background colors with HSV sliders, HEX input, swap, contrast warnings, and session-only recent colors
- Immersive Display mode, maximum window brightness, and keep-screen-on behavior
- Tap-to-show Exit overlay
- Settings action and a minimal Settings screen with system-language information, dynamic app version, studio details, and truthful Privacy Policy/Open Source Licenses placeholders

Planned:

- Dot-Matrix rendering
- Persistent color and editor preferences
- AdMob and UMP consent support
- Firebase Analytics and Crashlytics
- In-app language switching and final production legal content

## Milestone 1 scope

The current MVP supports editing a message with at most one explicit line break, choosing text styles and colors, and independently combining Static or bidirectional marquee motion with None, Blink, Pulse, or Fade in both the Editor preview and the landscape fullscreen Text + Glow display. Motion and Visual Effect transforms are composited independently. During marquee Pulse, scale is anchored to the direction-aware leading edge so it cannot alter the horizontal trajectory. Motion Speed and Effect Speed are independent 0.25x–5.0x controls: Motion Speed affects only horizontal marquee travel, and Effect Speed affects only Blink, Pulse, and Fade timing. Static output preserves the explicit break, may also auto-wrap, and fits the complete message on one or at most two horizontally and vertically centered lines without ellipsis. Marquee output remains sized primarily by height and always scrolls one rendered line, replacing the stored break with a space only at render time; switching back to Static restores the two-line behavior. Preview follows the same motion-aware fitting rule. Empty, whitespace-only, or newline-only input shows a preview-only `Hello!` sample without changing editor state, leaking into `DisplayConfig`, or enabling fullscreen display. The Settings screen reports the system-default language but does not switch languages in-app; its Privacy Policy and Open Source Licenses entries are placeholders. Dot Matrix is not implemented yet. Recent colors and speed selections last only for the current Editor session; there is no app-restart persistence, advertising, analytics, or network access.

## Technology stack

- Android Gradle Plugin 9.4.1
- Gradle 9.8.0
- Kotlin 2.4.21
- Jetpack Compose and Material 3
- Three Android activities (`EditorActivity`, `DisplayActivity`, and `SettingsActivity`)
- Gradle Kotlin DSL with a version catalog
- AndroidX Core SplashScreen
- minSdk 24, compileSdk 36, targetSdk 36
- JDK/JVM 17

## Project structure

```text
app/src/main/java/com/gwolbisa/ledgram/
├── model/          # Parcelable display configuration and enums
├── ui/
│   ├── components/ # Shared LED preview/text rendering
│   ├── display/    # Landscape fullscreen display
│   ├── editor/     # Portrait message editor
│   ├── settings/   # Minimal app information and legal placeholders
│   └── theme/      # Material theme and brand colors
└── util/           # Message, color, contrast, and recent-color utilities
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

