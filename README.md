[한국어](README.ko.md)

# LEDgram

![Android](https://img.shields.io/badge/Android-API%2024+-3DDC84?logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-2.4.21-7F52FF?logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?logo=jetpackcompose&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-9.8.0-02303A?logo=gradle&logoColor=white)
![JDK](https://img.shields.io/badge/JDK-17-ED8B00?logo=openjdk&logoColor=white)
![AdMob](https://img.shields.io/badge/Google%20Mobile%20Ads-25.5.0-4285F4?logo=googleadmob&logoColor=white)

Android LED signboard app for concerts, cheering, events, and quick fullscreen messages.

- Developer: `Gowl Labs`
- Package: `com.gwolbisa.ledgram`
- Version: `0.9.4` (`versionCode 13`)
- minSdk: 24
- compileSdk / targetSdk: 36 / 36

## Status

Core features are mostly done. The project is in release-prep.

- Editor → fullscreen Display
- Text + Glow / Dot Matrix
- Static / bidirectional Marquee
- Blink / Pulse / Fade
- live Motion Speed / Effect Speed changes
- Bold / Italic / Underline
- color presets / HSV / HEX / contrast warning
- Preferences DataStore
- Settings / Privacy Policy / Open Source Licenses / Reset
- Google Mobile Ads + UMP

## Editor

- up to 100 Unicode code points
- explicit line breaks preserved
- Text Color / Background Color
- preset colors + HSV + HEX
- color swap
- low-contrast warning
- recent colors are session-only
- Bold / Italic / Underline
- Render Style
  - Text + Glow
  - Dot Matrix
- Motion
  - Static
  - Right → Left
  - Left → Right
- Effect
  - None
  - Blink
  - Pulse
  - Fade
- Motion Speed / Effect Speed: `0.25x ~ 5.0x`
- Preview
- compact Preview header with Settings access; no full Editor app bar

When the message is empty, `Hello!` is shown in Preview only. It is not persisted or sent to Display.

## Display

- compact phone: landscape
- `sw600dp+`: adaptive to the current window
- status/navigation bars hidden
- maximum window brightness
- keep screen on
- tap to show icon-only controls
- session lock hides Exit and consumes Back until unlocked
- no ads in Display

## Multiline

- explicit line breaks preserved
- Static: auto-fit the whole multiline block
- Marquee: move the whole multiline block as one unit
- no automatic wrapping inside each marquee line

## Render Style

### Text + Glow

Uses Android system fonts and fallback fonts.

Korean, English, and other Unicode text render within system font support.

### Dot Matrix

Circular LED dots generated from pixel masks rasterized with the bundled Galmuri11 v2.40.4 font.

Supported:

- uppercase and lowercase Latin with input case preserved
- 0–9
- spaces
- modern precomposed Korean Hangul syllables (`U+AC00..U+D7A3`)
- Hangul Compatibility Jamo, including standalone consonants and vowels
- modern Hangul Jamo included by the font
- mixed Korean/English text

Galmuri11 is used only as the offline glyph-shape source; rendering remains LEDgram's circular-dot Canvas. Rasterized glyphs are cached by Unicode code point. Unsupported Unicode uses one `?` per code point.

Styles:

- Bold: one slightly larger dot with conservative spacing
- Italic: row-based X offset
- Underline: bottom dot row

Styled width/height is included in Static fitting and Marquee travel.

## Motion / Effect

Motion and Effect are independent.

Examples:

- Right → Left + Pulse
- Left → Right + Blink
- Static + Fade

Speed changes are independent too.

- Motion Speed: keep the current marquee position
- Effect Speed: keep the current effect phase
- both apply the new speed from the next frame

## DataStore

Editor state:

- message
- text/background colors
- Bold / Italic / Underline
- Render Style
- Motion / Effect
- Motion Speed / Effect Speed

Ad state:

- completed Display-exit count
- two recent interstitial timestamps

`Reset Editor Settings` resets Editor state only.
UMP consent, Display-exit count, and interstitial frequency state are preserved.

## Ads

### Banner

- Editor only
- 320×50 below `Start Display`
- button + banner footer hidden while keyboard is open
- none in Display

### Interstitial

- candidate every 5 real Display exits
- max 2 in a rolling 30-minute window
- unavailable / load failure / show failure does not block UX
- exit counter resets only after the ad actually starts showing

The current build uses official Google test App ID / banner ID / interstitial ID.
Production AdMob values will be added at release time.

## Privacy / Legal

Settings includes:

- Privacy Policy
- Open Source Licenses
- Third-party SDKs
- Privacy Options — only when UMP requires it

The Open Source Licenses screen shows library and license names only. Google advertising and consent SDKs remain in a separate Third-party SDKs notice.

Bundled font: [Galmuri11 v2.40.4](https://github.com/quiple/galmuri/releases/tag/v2.40.4), distributed under the SIL Open Font License 1.1. The upstream license text is packaged with the app.

Current setup:

- no user accounts
- no backend/server for user content
- no Firebase Analytics
- no Crashlytics
- no custom telemetry
- LED message text is not uploaded to a Gowl Labs server

Google Mobile Ads / Google UMP are used for ads and consent handling.

## Settings

- app name / versionName / Gowl Labs
- Language: System default / 한국어 / English
- Privacy Policy
- Open Source Licenses
- Privacy Options
- Reset Editor Settings

Korean Editor controls use localized labels for Preview, Render Style, Motion, Effect, and their speed controls. Product names and technical terms such as LEDgram, Gowl Labs, Display, HEX, SDK, and Google product names remain unchanged where appropriate.

## Tech stack

| Item | Version / configuration |
| --- | --- |
| AGP | 9.4.1 |
| Gradle | 9.8.0 |
| Kotlin | 2.4.21 |
| JDK / JVM | 17 |
| minSdk | 24 |
| compileSdk / targetSdk | 36 / 36 |
| UI | Jetpack Compose + Material 3 |
| Storage | Preferences DataStore 1.2.1 |
| Lifecycle | AndroidX Lifecycle 2.10.0 |
| Per-app locale | AndroidX AppCompat 1.7.1 |
| Ads | Google Mobile Ads SDK 25.5.0 |
| Consent | Google UMP SDK 4.0.0 |

Uses AGP 9 built-in Kotlin. Do not add `org.jetbrains.kotlin.android`.

## Structure

```text
app/src/main/java/com/gwolbisa/ledgram/
├── ads/
├── data/
├── model/
├── ui/
│   ├── components/
│   ├── display/
│   ├── editor/
│   ├── settings/
│   └── theme/
└── util/
```

## Build

```bash
./gradlew clean assembleDebug
./gradlew test
```

Optional:

```bash
./gradlew lintDebug
```

Debug APK:

```text
app/build/outputs/apk/debug/
```

## Workflow

1. define requirements
2. implement with Codex
3. inspect `git status` / `git diff`
4. build / test
5. verify on device
6. commit / push
7. next task

## Remaining

- app icon / Splash / branding cleanup
- production AdMob App ID / ad unit IDs
- production UMP consent message
- public Privacy Policy URL for Google Play
- release signing / AAB
- Play Console listing / store assets
- final regression test

## License

No project license selected yet.
