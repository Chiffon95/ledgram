# Google Play Store listing draft

This listing reflects LEDgram `0.9.8` (`versionCode 17`) and package `com.gwolbisa.ledgram`.

## 한국어 (`ko-KR`)

- **앱 이름:** LEDgram
- **개발자 이름:** Gowl Labs
- **지원 이메일:** 95doubleh@gmail.com
- **개인정보처리방침:** https://chiffon95.github.io/ledgram/privacy/

### 짧은 설명

> 문구와 색상, 움직임과 효과를 설정해 전체 화면으로 보여주는 LED 전광판 앱

문자 수: **43 / 80**

### 자세한 설명

```text
LEDgram은 광고로 운영되는 무료 Android LED 전광판 앱입니다. 입력한 문구를 미리 확인하고 전체 화면으로 표시할 수 있습니다. 콘서트, 응원, 행사 또는 큰 문구를 빠르게 보여줘야 할 때 사용할 수 있습니다.

주요 기능
• Text + Glow와 도트 전광판 표현 방식
• 글자색과 배경색 설정
• 고정, 오른쪽에서 왼쪽, 왼쪽에서 오른쪽 움직임
• 깜박임, 숨쉬기, 부드러운 점멸 효과
• 이동 속도와 효과 속도 개별 조절
• 굵게, 기울임, 밑줄 스타일
• 직접 입력한 줄바꿈을 유지하는 여러 줄 문구
• 전체 화면으로 전환하기 전 미리보기
• 기기에 로컬로 저장되는 Editor 설정

Text + Glow는 글자에 빛나는 느낌을 더해 표시합니다. 도트 전광판은 문구를 원형 LED 점으로 표현합니다. 움직임과 시각 효과는 서로 조합할 수 있으며 각각의 속도를 따로 조절할 수 있습니다.

Editor에서 설정한 문구, 색상, 스타일, 표현 방식, 움직임, 효과와 속도는 기기에 로컬로 저장되어 다음 실행 때 이어서 사용할 수 있습니다.

LEDgram은 한국어와 영어 UI를 지원합니다. 전체 화면 Display에는 광고가 표시되지 않습니다.
```

문자 수: **596 / 4,000**

## English (`en-US`)

- **App name:** LEDgram
- **Developer name:** Gowl Labs
- **Support email:** 95doubleh@gmail.com
- **Privacy policy:** https://chiffon95.github.io/ledgram/privacy/

### Short description

> Create and display customizable LED messages in full screen

Character count: **59 / 80**

### Full description

```text
LEDgram is a free, ad-supported Android LED signboard app. Preview your message, then show it in full screen for concerts, cheering, events, or any situation where you need to display a large message quickly.

Features
• Text + Glow and Dot Matrix display styles
• Custom text and background colors
• Static, right-to-left, and left-to-right motion
• Blink, Pulse, and Fade effects
• Separate motion and effect speed controls
• Bold, Italic, and Underline styles
• Multiline messages that preserve your line breaks
• Preview before entering full-screen display
• Editor preferences saved locally on your device

Text + Glow adds a luminous appearance to your message. Dot Matrix renders the message with circular LED dots. Motion and visual effects can be combined, with separate speed adjustments for each.

Your message, colors, styles, display style, motion, effects, and speed settings are stored locally on your device so you can continue with the same Editor setup the next time you open the app.

LEDgram supports Korean and English interfaces. Ads are not shown in the full-screen Display.
```

Character count: **1,097 / 4,000**

## Screenshot plan

Capture screenshots from the actual Android app. Do not use mockups that show controls, content, or behavior that is not present in the released build.

1. **Editor overview** — Portrait Editor with a real message, Preview, and the primary controls visible.
2. **Fullscreen Text + Glow** — Landscape Display showing a short, legible message with the Text + Glow style.
3. **Fullscreen Dot Matrix** — Landscape Display showing a Korean/English message rendered with circular LED dots.
4. **Motion and effect controls** — Editor view showing a scroll direction, Blink/Pulse/Fade selection, and the separate motion/effect speed controls.
5. **Custom colors** — Actual color controls with customized text/background colors reflected in Preview.
6. **Multiline display** — Fullscreen Display showing an intentional two-line message with explicit line breaks preserved.

Recommended launch order: Editor overview, Text + Glow, Dot Matrix, motion/effect controls, custom colors, multiline display. Capture all six if possible; use at least the first four high-quality captures.

### Captured Korean screenshots

These screenshots were captured from the unmodified `0.9.8` (`versionCode 17`) app on a physical Korean-language Android device. System bars and the Editor test-ad area were cropped where needed; no UI content was composited, stretched, or recreated.

| Order | Screenshot | Dimensions | Captured state |
| --- | --- | ---: | --- |
| 1 | [`screenshots/ko/01-editor-overview.png`](screenshots/ko/01-editor-overview.png) | 1080×1950 | Editor overview with `LEDgram` |
| 2 | [`screenshots/ko/02-text-glow.png`](screenshots/ko/02-text-glow.png) | 2160×1080 | Fullscreen Text + Glow with `LEDgram` |
| 3 | [`screenshots/ko/03-dot-matrix.png`](screenshots/ko/03-dot-matrix.png) | 2160×1080 | Fullscreen Dot Matrix with `응원해요!` |
| 4 | [`screenshots/ko/04-motion-effects.png`](screenshots/ko/04-motion-effects.png) | 1080×1720 | Bidirectional motion, Pulse, and separate speed controls |
| 5 | [`screenshots/ko/05-custom-colors.png`](screenshots/ko/05-custom-colors.png) | 1080×1850 | Custom color dialog with an actual orange selection |
| 6 | [`screenshots/ko/06-multiline.png`](screenshots/ko/06-multiline.png) | 2160×1080 | Fullscreen Text + Glow with `오늘도\n화이팅!` |

### Reproducible capture workflow

1. Connect a physical Android device or start an emulator, then select it from `adb devices -l`.
2. Install and launch the release-equivalent app. Use Korean as the system/app language for this set.
3. Configure each state through the real Editor UI. For Hangul on devices where `adb shell input text` cannot emit Korean, enter the message with the device keyboard before capture.
4. Capture the current screen without browser or desktop tooling:

   ```sh
   adb -s "$SERIAL" exec-out screencap -p > screenshot-raw.png
   ```

5. Crop only system bars, navigation controls, or the Editor test-ad area. Do not resize, stretch, composite, or add UI.
6. Export as 24-bit RGB PNG and verify dimensions, aspect ratio, readability, and absence of personal information or unrelated overlays.

## Google Play graphic requirements

- **Store icon:** 512×512, 32-bit PNG with alpha, maximum 1 MB.
- **Feature graphic:** 1024×500, JPEG or 24-bit PNG without alpha.
- **Screenshots:** At least 2 are required across supported device types; use at least 4 high-quality captures for this listing. Screenshots must be JPEG or 24-bit PNG without alpha, with each dimension from 320 px to 3,840 px and the longer dimension no more than twice the shorter dimension.
- All screenshots and graphics must accurately reflect the released app and must not imply unsupported functionality.

## Finalized graphic assets

- Approved icon source: `assets/LEDgram_icon.png`
- Google Play icon: `assets/LEDgram_icon_512.png`
- Approved feature graphic source: `assets/LEDgram_feature.png`
- Google Play feature graphic: `assets/LEDgram_feature_1024x500.png`

The approved source files are preserved unchanged. The exported assets use the required dimensions, retain the original aspect ratio, and use the artwork's matching black background.

## Remaining asset work

- No additional manual capture is required for the Korean screenshot set.
- Capture a separate English set only if an English-localized screenshot listing is desired; do not translate or edit the Korean captures.
- Before Play Console upload, review the final assets once more against the exact submitted build.

Requirements checked against Google Play Console Help:

- [Create and set up your app](https://support.google.com/googleplay/android-developer/answer/9859152)
- [Add preview assets to showcase your app](https://support.google.com/googleplay/android-developer/answer/9866151)
