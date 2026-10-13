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
- Text + Glow and programmatic 5×7 Dot Matrix rendering with independently composited Motion (Static, R→L, L→R) and Visual Effect (None, Blink, Pulse, Fade) selectors; marquee Pulse scaling uses a direction-aware leading-edge pivot so visual effects do not alter the trajectory
- Separate Motion Speed and Effect Speed controls with selectable values from 0.25x to 5.0x; motion speed changes only marquee travel, while effect speed changes the currently running Blink, Pulse, or Fade immediately without resetting its phase
- Static display preserves every explicit line break, may auto-wrap, and shrinks the complete centered multiline block to fit without an imposed line-count limit, ellipsis, truncation, or clipping; marquees also preserve explicit lines and move the complete centered multiline block horizontally as one unit, without automatic wrapping inside a logical line
- Dot Matrix supports A–Z (lowercase maps to uppercase), digits, spaces, and common punctuation (`. , ! ? : ; - _ + / \\ ( ) [ ] # @`); unsupported Unicode code points use a `?` fallback glyph without affecting Text + Glow’s system-font support
- Dot Matrix fits Static blocks to width and height, fits marquee blocks by height while allowing horizontal overflow, centers shorter logical lines within the widest line, and applies visual effects to the complete Canvas block; Bold, Italic, and Underline are ignored and disabled while selected but remain stored for Text + Glow
- Bold, italic, and underline text styles
- Basic and custom text/background colors with HSV sliders, HEX input, swap, contrast warnings, and session-only recent colors
- Preferences DataStore persistence for the stored multiline message, text/background colors, text styles, render style, Motion, Visual Effect, Motion Speed, and Effect Speed; the preview-only `Hello!` sample and recent colors are not persisted
- Immersive Display mode, maximum window brightness, and keep-screen-on behavior
- Tap-to-show Exit overlay
- Settings action and a minimal Settings screen with system-language information, dynamic app version, studio details, and truthful Privacy Policy/Open Source Licenses placeholders

Planned:

- AdMob and UMP consent support
- Firebase Analytics and Crashlytics
- In-app language switching and final production legal content

## Milestone 1 scope

The current MVP supports editing a 100-code-point message with any number of explicit line breaks, choosing colors and text styles, and selecting Text + Glow or Dot Matrix in both the Editor preview and the landscape fullscreen display. Static or bidirectional marquee motion combines independently with None, Blink, Pulse, or Fade. A frame-clock-based visual phase is shared by both render styles; changing Effect Speed updates phase progression on the next frame without resetting the current alpha or scale, while marquee motion remains independent. During marquee Pulse, scale is anchored to the direction-aware leading edge of the complete block so it cannot alter the horizontal trajectory. Motion Speed and Effect Speed are independent 0.25x–5.0x controls. Text + Glow Static output preserves all explicit breaks, may auto-wrap, and shrinks the complete centered block to fit. Its marquee preserves explicit lines, disables automatic wrapping within each logical line, fits vertically, and moves all lines together. Dot Matrix uses a programmatic 5×7 glyph grid and the same multiline block semantics: Static fits width and height, while marquee fits height and uses the stable unscaled widest-line block width for travel. Supported Dot Matrix characters are A–Z, a–z through uppercase mapping, 0–9, spaces, and common punctuation; unsupported code points use `?`. Dot Matrix ignores Bold, Italic, and Underline without clearing them. Preferences DataStore preserves the message, colors, text styles, render style, motion/effect selections, and both speeds across app restarts. Empty or whitespace-only input shows a preview-only `Hello!` sample without changing or persisting the editor message, leaking into `DisplayConfig`, or enabling fullscreen display. Recent colors remain session-only. The Settings screen reports the system-default language but does not switch languages in-app; its Privacy Policy and Open Source Licenses entries are placeholders. There is no advertising, analytics, or network access.

## Technology stack

- Android Gradle Plugin 9.4.1
- Gradle 9.8.0
- Kotlin 2.4.21
- Jetpack Compose and Material 3
- Three Android activities (`EditorActivity`, `DisplayActivity`, and `SettingsActivity`)
- Gradle Kotlin DSL with a version catalog
- AndroidX Core SplashScreen
- AndroidX Preferences DataStore 1.2.1
- AndroidX Lifecycle 2.10.0
- minSdk 24, compileSdk 36, targetSdk 36
- JDK/JVM 17

## Project structure

```text
app/src/main/java/com/gwolbisa/ledgram/
├── data/           # Preferences DataStore mapping and repository
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

Milestone 1 requests no network permission and includes no ads, consent SDK, analytics, crash reporting, or cloud upload. Preferences DataStore keeps only local Editor UI settings and the user-entered message; message content is not logged. Planned network integrations must receive an explicit privacy review before they are introduced.

## License

No open-source license has been selected yet. All rights are reserved unless a license is added later.

