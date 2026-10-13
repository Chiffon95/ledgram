[English](README.md)

# LEDgram

LEDgram은 휴대폰 화면을 콘서트, 응원, 행사 및 간단한 시각 메시지 전달용 맞춤형 전체화면 LED 전광판으로 사용할 수 있게 하는 Android 앱입니다.

- **앱 이름:** LEDgram
- **패키지:** `com.gwolbisa.ledgram`
- **스튜디오:** GWOL LABS

## 프로젝트 상태

LEDgram은 초기 개발 단계입니다. 마일스톤 1에서 처음으로 동작하는 수직 기능 흐름을 제공합니다.

구현됨:

- 단일 모듈 Android 프로젝트 골격
- 세로형 Editor → 가로형 전체화면 Display 흐름
- 입력이 비어 있을 때 샘플을 보여 주는 실시간 Editor 미리보기
- 정적, 양방향 흐르기, 깜박임, 펄스, 페이드 효과와 굵게, 기울임, 밑줄을 지원하는 Text + Glow 렌더링
- HSV 슬라이더, HEX 입력, 색상 교환, 대비 경고 및 세션 한정 최근 색상을 지원하는 기본/사용자 지정 글자색·배경색
- 몰입형 Display 모드, 창 최대 밝기 및 화면 켜짐 유지
- 탭하면 나타나는 Exit 오버레이

계획됨:

- Dot-Matrix 렌더링
- 효과 속도 조절
- 색상 및 Editor 설정 영구 저장
- AdMob 및 UMP 동의 지원
- Firebase Analytics 및 Crashlytics
- 언어 설정 및 최종 프로덕션 브랜딩

## 마일스톤 1 범위

현재 MVP는 한 줄 메시지 편집과 미리보기, 글자 스타일과 색상 선택, 그리고 정적·왼쪽에서 오른쪽 흐르기·오른쪽에서 왼쪽 흐르기·깜박임·펄스·페이드 Text + Glow 출력을 가로형 전체화면으로 표시하는 기능을 지원합니다. Editor 미리보기는 정적으로 유지됩니다. Dot Matrix 및 효과 속도 조절은 아직 구현되지 않았습니다. 최근 색상은 현재 Editor 세션에서만 유지되며 앱 재시작 후 설정 저장, 광고, 분석 및 네트워크 접근은 포함하지 않습니다.

## 기술 스택

- Kotlin
- Jetpack Compose 및 Material 3
- 두 개의 Android Activity (`EditorActivity`, `DisplayActivity`)
- 버전 카탈로그를 사용하는 Gradle Kotlin DSL
- AndroidX Core SplashScreen
- minSdk 24, compileSdk 36, targetSdk 36
- JVM 17

## 프로젝트 구조

```text
app/src/main/java/com/gwolbisa/ledgram/
├── model/          # Parcelable 표시 설정 및 enum
├── ui/
│   ├── components/ # 공유 LED 미리보기/텍스트 렌더링
│   ├── display/    # 가로형 전체화면 표시
│   ├── editor/     # 세로형 메시지 편집기
│   └── theme/      # Material 테마 및 브랜드 색상
└── util/           # 메시지, 색상, 대비 및 최근 색상 유틸리티
```

## 요구 사항

- Android SDK Platform 36이 설치된 Android Studio
- JDK 17
- Android 7.0(API 24) 이상을 실행하는 Android 기기 또는 에뮬레이터

## 로컬 빌드

저장소 루트에서 다음 명령을 실행합니다.

```bash
./gradlew assembleDebug
./gradlew testDebugUnitTest
./gradlew lintDebug
```

디버그 APK는 `app/build/outputs/apk/debug/` 아래에 생성되며 Git에서 제외됩니다.

## 개발 워크플로

1. `main`에서 목적이 명확한 브랜치를 만듭니다.
2. 마일스톤 변경을 작게 유지하고 추측성 인프라를 추가하지 않습니다.
3. 리뷰 요청 전에 빌드, 단위 테스트 및 lint를 실행합니다.
4. diff를 검토하고 기기 또는 에뮬레이터에서 Editor → Display 흐름을 확인합니다.
5. 로컬 SDK 경로, 서명 파일, 자격 증명 또는 생성된 바이너리를 커밋하지 않습니다.

## 개인정보 보호 및 분석

마일스톤 1은 네트워크 권한을 요청하지 않으며 광고, 동의 SDK, 분석, 비정상 종료 보고 또는 사용자 데이터 저장을 포함하지 않습니다. 계획된 통합 기능은 도입 전에 명시적인 개인정보 보호 검토를 거쳐야 합니다.

## 라이선스

아직 오픈 소스 라이선스를 선택하지 않았습니다. 추후 라이선스가 추가되기 전까지 모든 권리를 보유합니다.

