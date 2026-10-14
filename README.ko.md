[English](README.md)

# LEDgram

휴대폰 화면을 **콘서트·응원·행사용 LED 전광판**처럼 쓰는 Android 앱입니다.

문구를 입력하고 색상, 표시 방식, 움직임, 효과를 고른 뒤 `전체 화면으로 보기`를 누르면 바로 전광판처럼 사용할 수 있습니다.

- **앱 이름:** LEDgram
- **개발자:** GWOL LABS
- **패키지:** `com.gwolbisa.ledgram`
- **최소 지원:** Android 7.0 / API 24

## 지금 어디까지 됐나

핵심 MVP는 거의 끝났습니다.

- Editor → fullscreen Display 흐름 구현
- Text + Glow / Dot Matrix 구현
- Static / 양방향 Marquee 구현
- Blink / Pulse / Fade 구현
- Motion Speed / Effect Speed 실시간 변경 구현
- Dot Matrix Bold / Italic / Underline 구현
- 색상 선택, HEX 입력, 대비 경고 구현
- Preferences DataStore 저장/복원 구현
- Display 최대 밝기 + 화면 꺼짐 방지 + immersive fullscreen 구현
- Settings / Privacy Policy / Open Source Licenses / Reset 구현
- Google Mobile Ads + UMP 기반 구현
- Editor 배너 광고 / Display 종료 후 전면 광고 정책 구현

현재 빌드는 **Google 공식 테스트 광고 ID만 사용**합니다. 프로덕션 AdMob ID는 아직 넣지 않았습니다.

## 주요 기능

### Editor

- 최대 **100 Unicode code points** 문구 입력
- 사용자가 넣은 줄바꿈 그대로 저장
- Text Color / Background Color 선택
- 기본 색상 + HSV + HEX 직접 입력
- 색상 교환
- 저대비 색상 경고
- 최근 색상은 현재 세션에서만 유지
- Bold / Italic / Underline
- Render Style 선택
  - `Text + Glow`
  - `Dot Matrix`
- Motion 선택
  - 고정
  - 오른쪽 → 왼쪽
  - 왼쪽 → 오른쪽
- Effect 선택
  - 없음
  - 깜박임
  - 숨쉬기
  - 부드러운 점멸
- Motion Speed / Effect Speed 각각 `0.25x ~ 5.0x`
- 상단 Live Preview에서 실제 Display 결과 미리보기

빈 문구일 때 Preview에는 `Hello!`가 보이지만, 실제 Editor 값에는 저장되지 않고 fullscreen Display도 실행되지 않습니다.

### Display

- 휴대폰 화면 전체를 전광판처럼 사용
- 일반 폰에서는 landscape 고정
- `sw600dp+` 대화면에서는 Android 정책에 맞춰 현재 window에 adaptive 대응
- 상태바 / 내비게이션바 숨김
- Window 밝기 최대 적용
- 화면 꺼짐 방지
- 화면 탭 시 Exit UI 표시
- **Display 안에는 광고 없음**

### 여러 줄 문구

Static과 Marquee 모두 사용자가 입력한 줄바꿈을 유지합니다.

- **Static:** 전체 여러 줄 블록이 화면 안에 들어오도록 자동 축소
- **Marquee:** 각 줄이 따로 움직이지 않고 여러 줄 전체가 하나의 블록으로 이동
- Marquee에서는 줄 내부 자동 줄바꿈 없이 사용자가 입력한 줄 구조를 그대로 사용

### Text + Glow

Android 시스템 폰트와 fallback을 사용합니다.

한글·영문·기타 Unicode는 시스템 폰트가 지원하는 범위에서 그대로 표시됩니다.

### Dot Matrix

프로그래밍 방식의 5×7 glyph를 사용합니다.

지원:

- A–Z
- a–z → 대문자로 변환
- 0–9
- 공백
- 일반 문장부호: `. , ! ? : ; - _ + / \\ ( ) [ ] # @`

지원하지 않는 문자는 `?` glyph로 표시합니다.

Dot Matrix에서도 다음 스타일을 사용할 수 있습니다.

- **Bold:** 활성 dot을 가로 방향으로 확장
- **Italic:** 행별 X offset으로 기울임
- **Underline:** 글자 아래 dot row 추가

스타일 때문에 늘어난 실제 폭/높이도 Static fitting과 Marquee 이동거리에 반영됩니다.

## 애니메이션 동작

Motion과 Effect는 서로 독립적으로 동작합니다.

예를 들어:

- 오른쪽 → 왼쪽 + 숨쉬기
- 왼쪽 → 오른쪽 + 깜박임
- 고정 + 부드러운 점멸

모두 가능합니다.

### 속도 변경

실행 중에도 속도를 바꿀 수 있습니다.

- Motion Speed 변경 → 현재 Marquee 위치를 유지한 채 다음 프레임부터 새 속도 적용
- Effect Speed 변경 → 현재 Blink/Pulse/Fade 위상을 유지한 채 다음 프레임부터 새 속도 적용

속도를 바꿨다고 애니메이션이 처음 위치로 돌아가지 않습니다.

## 저장되는 값

Preferences DataStore에 저장합니다.

Editor 설정:

- 문구
- 글자색 / 배경색
- Bold / Italic / Underline
- Render Style
- Motion / Effect
- Motion Speed / Effect Speed

광고 정책용 로컬 상태:

- Display 종료 횟수
- 최근 전면 광고 표시 시각 2개

`Editor 설정 초기화`는 **Editor 설정만 초기화**합니다.

다음은 초기화하지 않습니다.

- Google UMP 동의 상태
- Display 종료 횟수
- 전면 광고 frequency 상태

## 광고 정책

현재 정책은 단순하게 잡았습니다.

### Banner

- Editor의 `전체 화면으로 보기` 버튼 아래에 320×50 배너
- 키보드가 열리면 버튼 + 배너 영역 전체 숨김
- Display에는 배너 없음

### Interstitial

- Display를 실제로 종료하고 Editor로 돌아온 횟수를 카운트
- **5회마다 전면 광고 후보**
- 최근 30분 동안 성공적으로 표시된 전면 광고는 **최대 2회**
- 광고가 없거나 로드/표시에 실패하면 앱 사용을 막지 않음
- 실제 광고가 표시된 경우에만 종료 카운터 초기화

## 개인정보 / 법적 정보

Settings에서 앱 안에서 바로 확인할 수 있습니다.

- Privacy Policy
- Open Source Licenses
- Third-party SDKs
- Privacy Options — UMP가 필요한 경우에만 표시

현재 앱은:

- 사용자 계정 없음
- 사용자 콘텐츠용 backend/server 없음
- Firebase Analytics 없음
- Crashlytics 없음
- 자체 telemetry 없음
- 입력한 LED 문구를 GWOL LABS 서버로 업로드하지 않음

Google Mobile Ads와 Google UMP는 광고/동의 처리를 위해 포함되어 있으며, Google 쪽 SDK는 Google의 관련 정책과 약관이 적용됩니다.

## Settings

현재 제공:

- LEDgram 앱 정보
- 실제 `versionName`
- Developer: `GWOL LABS`
- 시스템 기본 언어 안내
- Privacy Policy
- Open Source Licenses
- 조건부 Privacy Options
- Editor 설정 초기화

한국어 UI에서도 제품/기술 라벨은 필요 이상으로 번역하지 않습니다.

예:

`Render Style`, `Text + Glow`, `Dot Matrix`, `Motion`, `Effect`, `Motion Speed`, `Effect Speed`

설명이나 사용자 동작은 자연스러운 한국어를 사용합니다.

## 기술 스택

| 항목 | 버전 / 구성 |
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

AGP 9의 built-in Kotlin을 사용합니다. `org.jetbrains.kotlin.android` 플러그인은 추가하지 않습니다.

## 프로젝트 구조

```text
app/src/main/java/com/gwolbisa/ledgram/
├── ads/            # UMP, banner, interstitial, 광고 정책
├── data/           # Preferences DataStore / repository
├── model/          # DisplayConfig / enums
├── ui/
│   ├── components/ # 공용 LED renderer / preview
│   ├── display/    # fullscreen Display
│   ├── editor/     # Editor
│   ├── settings/   # Settings / Privacy / OSS notices
│   └── theme/      # Material theme / brand colors
└── util/           # message / color / speed helpers
```

## 로컬 빌드

필요 환경:

- Android Studio
- Android SDK Platform 36
- JDK 17
- Android 7.0(API 24) 이상 기기 또는 emulator

```bash
./gradlew clean assembleDebug
./gradlew test
```

추가 확인이 필요하면:

```bash
./gradlew lintDebug
```

Debug APK:

```text
app/build/outputs/apk/debug/
```

## 개발 방식

이 프로젝트는 기능을 한 번에 크게 넣기보다 작은 단위로 구현하고 바로 검증하는 식으로 진행합니다.

1. 요구사항 확정
2. Codex로 구현
3. `git status` / `git diff` 확인
4. build / test
5. 실제 기기 확인
6. commit / push
7. 다음 기능 진행

## 출시 전에 남은 것

핵심 앱 기능보다 **출시 준비 작업**이 남아 있습니다.

- 앱 아이콘 / Splash / 최종 브랜딩
- 프로덕션 AdMob App ID / ad unit ID 적용
- UMP 프로덕션 consent message 구성
- Google Play용 공개 Privacy Policy URL
- release signing / AAB
- Play Console 등록 정보와 스토어 이미지
- 최종 실기기 regression test

앱 내 언어 전환은 현재 필수 MVP에서는 제외되어 있고, 이후 필요하면 추가할 예정입니다.

## 광고 테스트 주의

**현재 repository의 광고 ID는 전부 Google 공식 TEST ID입니다.**

프로덕션 출시 전에는 App ID와 banner/interstitial ad unit ID를 한 세트로 실제 AdMob 값으로 바꿔야 합니다.

테스트 ID와 프로덕션 ID를 섞어서 쓰지 않습니다.

## License

현재 LEDgram 자체의 오픈소스 라이선스는 별도로 선택하지 않았습니다.

별도 라이선스를 추가하기 전까지는 All rights reserved입니다.
