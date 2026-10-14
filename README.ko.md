[English](README.md)

# LEDgram

![Android](https://img.shields.io/badge/Android-API%2024+-3DDC84?logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-2.4.21-7F52FF?logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?logo=jetpackcompose&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-9.8.0-02303A?logo=gradle&logoColor=white)
![JDK](https://img.shields.io/badge/JDK-17-ED8B00?logo=openjdk&logoColor=white)
![AdMob](https://img.shields.io/badge/Google%20Mobile%20Ads-25.5.0-4285F4?logo=googleadmob&logoColor=white)

휴대폰 화면을 콘서트·응원·행사용 LED 전광판처럼 쓰는 Android 앱.

- 개발자: `Gowl Labs`
- Package: `com.gwolbisa.ledgram`
- Version: `0.9.1` (`versionCode 10`)
- minSdk: 24
- compileSdk / targetSdk: 36 / 36

## 상태

핵심 기능은 거의 끝났고 현재 출시 준비 중.

- Editor → fullscreen Display
- Text + Glow / Dot Matrix
- Static / 양방향 Marquee
- Blink / Pulse / Fade
- Motion Speed / Effect Speed 실시간 변경
- Bold / Italic / Underline
- 색상 선택 / HSV / HEX / 대비 경고
- Preferences DataStore 저장
- Settings / Privacy Policy / Open Source Licenses / Reset
- Google Mobile Ads + UMP

## Editor

- 최대 100 Unicode code points
- 입력한 줄바꿈 그대로 저장
- Text Color / Background Color
- 기본 색상 + HSV + HEX
- 색상 교환
- 저대비 경고
- 최근 색상은 세션 단위
- Bold / Italic / Underline
- Render Style
  - Text + Glow
  - Dot Matrix
- Motion
  - 고정
  - 오른쪽 → 왼쪽
  - 왼쪽 → 오른쪽
- Effect
  - 없음
  - 깜박임
  - 숨쉬기
  - 부드러운 점멸
- Motion Speed / Effect Speed: `0.25x ~ 5.0x`
- Live Preview
- 전체 Editor 앱 바 없이 Settings로 이동할 수 있는 compact Preview header

빈 문구는 Preview에서만 `Hello!` 표시. 저장값이나 Display에는 들어가지 않음.

## Display

- compact phone: landscape
- `sw600dp+`: 현재 window 기준 adaptive
- 상태바 / 내비게이션바 숨김
- window 밝기 최대
- keep screen on
- 탭하면 icon-only control 표시
- session lock 중에는 Exit를 숨기고 잠금 해제 전까지 Back 차단
- Display에는 광고 없음

## 여러 줄

- 입력한 줄바꿈 유지
- Static: 여러 줄 전체가 화면 안에 들어오도록 auto-fit
- Marquee: 여러 줄 전체를 하나의 block으로 이동
- Marquee 줄 내부 자동 줄바꿈 없음

## Render Style

### Text + Glow

Android system font + fallback 사용.

한글/영문/기타 Unicode는 시스템 폰트 지원 범위에서 그대로 표시.

### Dot Matrix

5×7 programmatic glyph.

지원:

- A–Z
- a–z → uppercase
- 0–9
- 공백
- `. , ! ? : ; - _ + / \\ ( ) [ ] # @`

미지원 문자는 `?`로 표시.

스타일:

- Bold: 간격을 유지한 하나의 조금 더 큰 dot
- Italic: row별 X offset
- Underline: 하단 dot row

스타일 때문에 늘어난 폭/높이도 Static fitting과 Marquee travel에 포함.

## Motion / Effect

Motion과 Effect는 독립 조합.

예:

- 오른쪽 → 왼쪽 + 숨쉬기
- 왼쪽 → 오른쪽 + 깜박임
- 고정 + 부드러운 점멸

속도 변경도 서로 독립.

- Motion Speed 변경: 현재 Marquee 위치 유지
- Effect Speed 변경: 현재 effect phase 유지
- 둘 다 다음 frame부터 새 속도 적용

## DataStore

Editor 상태:

- 문구
- 글자색 / 배경색
- Bold / Italic / Underline
- Render Style
- Motion / Effect
- Motion Speed / Effect Speed

광고 상태:

- Display 종료 횟수
- 최근 interstitial 표시 시각 2개

`Editor 설정 초기화`는 Editor 상태만 초기화.
UMP 동의 / Display 종료 횟수 / interstitial frequency 상태는 유지.

## 광고

### Banner

- Editor에서만 표시
- `전체 화면으로 보기` 아래 320×50
- 키보드 열리면 버튼 + banner footer 숨김
- Display에는 없음

### Interstitial

- 실제 Display 종료 5회마다 후보
- rolling 30분 최대 2회
- unavailable / load 실패 / show 실패 시 UX 차단 없음
- 실제 표시 시작 시에만 종료 카운터 reset

현재는 Google 공식 test App ID / banner ID / interstitial ID 사용 중.
실제 출시 단계에서 production AdMob 값으로 교체 예정.

## Privacy / Legal

Settings에 포함:

- Privacy Policy
- Open Source Licenses
- Third-party SDKs
- Privacy Options — UMP가 필요할 때만

Open Source Licenses 화면에는 library와 license 이름만 표시. Google 광고/동의 SDK는 별도의 Third-party SDKs 고지로 구분.

현재 구성:

- 사용자 계정 없음
- 사용자 콘텐츠용 backend/server 없음
- Firebase Analytics 없음
- Crashlytics 없음
- 자체 telemetry 없음
- LED 문구를 Gowl Labs 서버로 업로드하지 않음

Google Mobile Ads / Google UMP는 광고와 동의 처리용.

## Settings

- 앱 이름 / versionName / Gowl Labs
- Language: 시스템 기본값 / 한국어 / English
- Privacy Policy
- Open Source Licenses
- Privacy Options
- Editor 설정 초기화

한국어 UI에서도 제품/기술 용어는 필요 이상으로 번역하지 않음.

`Render Style`, `Text + Glow`, `Dot Matrix`, `Motion`, `Effect`, `Motion Speed`, `Effect Speed`

## 기술 스택

| 항목 | 버전 / 구성 |
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

AGP 9 built-in Kotlin 사용. `org.jetbrains.kotlin.android`는 추가하지 않음.

## 구조

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

필요하면:

```bash
./gradlew lintDebug
```

Debug APK:

```text
app/build/outputs/apk/debug/
```

## 개발 흐름

1. 요구사항 정리
2. Codex 구현
3. `git status` / `git diff`
4. build / test
5. 실기기 확인
6. commit / push
7. 다음 작업

## 남은 작업

- 앱 아이콘 / Splash / 브랜딩 정리
- production AdMob App ID / ad unit ID
- UMP production consent message
- Google Play용 Privacy Policy URL
- release signing / AAB
- Play Console listing / store assets
- 최종 regression test

## License

LEDgram 자체 라이선스는 아직 별도 지정 안 함.
