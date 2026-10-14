[한국어](README.ko.md)

# LEDgram

LEDgram is an Android app that turns a phone screen into a **fullscreen LED signboard** for concerts, cheering, events, and quick visual messages.

Type a message, choose colors, rendering, motion, and effects, then tap `Start Display` to use the phone like a signboard.

- **App name:** LEDgram
- **Developer:** GWOL LABS
- **Package:** `com.gwolbisa.ledgram`
- **Minimum Android:** Android 7.0 / API 24

## Where the project is now

The core MVP is almost complete.

- Editor → fullscreen Display flow
- Text + Glow / Dot Matrix
- Static / bidirectional Marquee
- Blink / Pulse / Fade
- live Motion Speed / Effect Speed changes
- Dot Matrix Bold / Italic / Underline
- color picker, HEX input, contrast warning
- Preferences DataStore persistence
- maximum Display brightness + keep-screen-on + immersive fullscreen
- Settings / Privacy Policy / Open Source Licenses / Reset
- Google Mobile Ads + UMP foundation
- Editor banner and post-Display interstitial policy

The current build uses **official Google test ad IDs only**. Production AdMob IDs are not configured yet.

## Main features

### Editor

- Up to **100 Unicode code points**
- Explicit line breaks are preserved
- Text Color / Background Color
- Preset colors + HSV + direct HEX input
- Swap colors
- Low-contrast warning
- Recent colors are session-only
- Bold / Italic / Underline
- Render Style
  - `Text + Glow`
  - `Dot Matrix`
- Motion
  - Static
  - Right → Left
  - Left → Right
- Effect
  - None
  - Blink
  - Pulse
  - Fade
- Separate Motion Speed / Effect Speed from `0.25x` to `5.0x`
- Live Preview at the top of the Editor

When the stored message is empty, Preview shows `Hello!` as a sample only. It is not persisted and fullscreen Display remains disabled.

### Display

- Uses the phone screen as the signboard
- Landscape on compact phones
- Adaptive behavior on `sw600dp+` large screens where Android may ignore fixed orientation
- Hides status/navigation bars
- Sets window brightness to maximum
- Keeps the screen on
- Tap to reveal the Exit UI
- **No ads inside Display**

### Multiline messages

Static and Marquee both keep the user's explicit line structure.

- **Static:** shrinks the whole multiline block until it fits the viewport
- **Marquee:** moves the entire multiline block as one unit
- No automatic wrapping inside each logical marquee line

### Text + Glow

Uses Android system fonts and fallback fonts.

Korean, English, and other Unicode text render when supported by the system font stack.

### Dot Matrix

Uses a programmatic 5×7 glyph set.

Supported:

- A–Z
- a–z mapped to uppercase
- 0–9
- spaces
- common punctuation: `. , ! ? : ; - _ + / \\ ( ) [ ] # @`

Unsupported code points use a `?` fallback glyph.

Dot Matrix also supports:

- **Bold:** extra horizontal active-dot offset
- **Italic:** row-based X offset
- **Underline:** an extra dot row below the line

Styled width/height is included in Static fitting and Marquee travel bounds.

## Animation behavior

Motion and Effect are independent.

Examples:

- Right → Left + Pulse
- Left → Right + Blink
- Static + Fade

All combinations are supported.

### Live speed changes

Speed controls apply to the running animation without restarting it.

- Motion Speed change → preserves current marquee position and changes movement from the next frame
- Effect Speed change → preserves the current Blink/Pulse/Fade phase and changes timing from the next frame

Changing speed does not jump the animation back to its initial state.

## Local persistence

Preferences DataStore stores:

Editor state:

- message
- text/background colors
- Bold / Italic / Underline
- Render Style
- Motion / Effect
- Motion Speed / Effect Speed

Local monetization state:

- completed Display-exit count
- two most recent successful interstitial timestamps

`Reset Editor Settings` resets **Editor preferences only**.

It does not reset:

- Google UMP consent state
- Display-exit count
- interstitial frequency state

## Ad policy

The monetization rules are intentionally simple.

### Banner

- 320×50 banner below `Start Display` in Editor
- the whole button + banner footer is hidden while the keyboard is open
- no banner in Display

### Interstitial

- counts real completed Display sessions
- **candidate every 5 Display exits**
- at most **2 successfully shown interstitials in a rolling 30-minute window**
- unavailable/load/show failures never block normal app use
- the exit counter resets only after the interstitial actually starts showing

## Privacy and legal

Available directly inside Settings:

- Privacy Policy
- Open Source Licenses
- Third-party SDKs
- Privacy Options — shown only when UMP requires it

Current app behavior:

- no user accounts
- no backend/server for user content
- no Firebase Analytics
- no Crashlytics
- no custom telemetry
- LED message text is not uploaded to a GWOL LABS server

Google Mobile Ads and Google UMP are included for advertising and consent handling and are governed by Google's applicable terms and policies.

## Settings

Currently includes:

- LEDgram app information
- actual `versionName`
- Developer: `GWOL LABS`
- system-default language information
- Privacy Policy
- Open Source Licenses
- conditional Privacy Options
- Reset Editor Settings

The Korean UI intentionally keeps product/technical labels in English when that reads better.

Examples:

`Render Style`, `Text + Glow`, `Dot Matrix`, `Motion`, `Effect`, `Motion Speed`, `Effect Speed`

Descriptions and user actions remain natural Korean.

## Technology stack

| Item | Version / configuration |
| --- | --- |
| Android Gradle Plugin | 9.4.1 |
| Gradle | 9.8.0 |
| Kotlin | 2.4.21 |
| JDK / JVM | 17 |
| minSdk | 24 |
| compileSdk / targetSdk | 36 / 36 |
| UI | Jetpack Compose + Material 3 |
| Local storage | Preferences DataStore 1.2.1 |
| Lifecycle | AndroidX Lifecycle 2.10.0 |
| Ads | Google Mobile Ads SDK 25.5.0 |
| Consent | Google UMP SDK 4.0.0 |

The project uses AGP 9 built-in Kotlin. Do not add `org.jetbrains.kotlin.android`.

## Project structure

```text
app/src/main/java/com/gwolbisa/ledgram/
├── ads/            # UMP, banner, interstitial, ad policy
├── data/           # Preferences DataStore / repository
├── model/          # DisplayConfig / enums
├── ui/
│   ├── components/ # shared LED renderer / preview
│   ├── display/    # fullscreen Display
│   ├── editor/     # Editor
│   ├── settings/   # Settings / Privacy / OSS notices
│   └── theme/      # Material theme / brand colors
└── util/           # message / color / speed helpers
```

## Local build

Requirements:

- Android Studio
- Android SDK Platform 36
- JDK 17
- Android 7.0 (API 24) or newer device/emulator

```bash
./gradlew clean assembleDebug
./gradlew test
```

Optional lint check:

```bash
./gradlew lintDebug
```

Debug APK:

```text
app/build/outputs/apk/debug/
```

## Development workflow

The project is built in small milestones and validated immediately instead of stacking large speculative changes.

1. Confirm requirements
2. Implement with Codex
3. Inspect `git status` / `git diff`
4. Build / test
5. Verify on a real device
6. Commit / push
7. Move to the next milestone

## What is left before release

Most remaining work is **release preparation**, not core app functionality.

- app icon / Splash / final branding
- production AdMob App ID / ad unit IDs
- production UMP consent message configuration
- public Privacy Policy URL for Google Play
- release signing / AAB
- Play Console listing and store assets
- final real-device regression test

In-app language switching is not required for the current MVP and may be added later if needed.

## Test ad warning

**All ad IDs currently committed in the repository are official Google TEST IDs.**

Before production release, replace the App ID and banner/interstitial ad unit IDs together with the actual AdMob values.

Do not mix test IDs and production IDs accidentally.

## License

No open-source license has been selected for LEDgram itself yet.

All rights reserved until a project license is added.
