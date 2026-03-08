# CLAUDE.md

## Project Overview

Android diary application built as a modern-stack template. Users can create, edit, and delete
diary entries, browse them on a calendar, and view random quotes fetched from a public API.
Supports dark/light/system themes and English/Vietnamese localization at runtime.

## Tech Stack

| Concern | Library / Tool |
|---------|---------------|
| Language | Kotlin (JVM 21) |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM + Clean Architecture |
| DI | Hilt |
| Async | Kotlin Coroutines + Flow |
| Networking | Retrofit 2 + OkHttp 3 + Sandwich |
| JSON | Moshi (KSP code-gen) |
| Local DB | Room (v1) |
| Navigation | Jetpack Navigation Compose |
| Build | Gradle Kotlin DSL (`.kts`) |
| Min / Target SDK | 26 / 36 |

## Key Directories

```
app/src/main/java/wade/owen/watts/base_jetpack/
├── core/
│   ├── designsystem/    # Reusable Compose components (AppButton, AppDialog, AppHeader)
│   ├── networks/        # OkHttp interceptors (ApiInterceptor)
│   ├── router/          # NavHost + BottomNavGraph definitions
│   ├── utils/           # DateTimeFormat, LocaleManager
│   └── viewmodel/       # BaseViewModel<S, E> — all ViewModels extend this
├── data/
│   ├── local/           # Room database, DAOs, SharedPrefs
│   ├── mapper/          # Extension functions: toDomain() / toEntity()
│   ├── models/entity/   # Room @Entity classes
│   ├── remote/          # Retrofit ApiService, ApiURL
│   └── repository/      # Repository implementations
├── di/                  # Hilt modules: NetworkModule, PersistenceModule, RepositoryModule
├── domain/
│   ├── entities/        # Pure domain models + enums (LoadStatus, AppTheme)
│   └── repository/      # Repository interfaces (contracts)
├── global/              # CompositionLocals (LocalMainViewModel)
└── ui/
    ├── main/            # MainActivity, MainPage, MainViewModel (theme/locale state)
    ├── pages/           # One sub-package per screen; each has Page + ViewModel + UiState + UiEvent
    ├── mapper/          # UI-layer mappers
    └── theme/           # Material 3 theme, colors, typography
```

## Build & Test Commands

```bash
# Build
./gradlew assembleDebug          # Debug APK
./gradlew assembleRelease        # Release APK (ProGuard enabled)
./gradlew build                  # All variants

# Test
./gradlew test                   # JVM unit tests  (app/src/test/)
./gradlew connectedAndroidTest   # Instrumented tests — requires running emulator/device

# Utilities
./gradlew lint                   # Run Android lint
./gradlew kspDebugKotlin         # Re-run KSP (Moshi / Room code-gen)
```

JVM heap for the build daemon is set to 2 GB in `gradle.properties`.

## Adding a New Screen

1. Create `ui/pages/<name>/` with `<Name>Page.kt`, `<Name>ViewModel.kt`,
   `<Name>UiState.kt`, `<Name>UiEvent.kt`
2. Extend `BaseViewModel<NameUiState, NameUiEvent>` — see `core/viewmodel/BaseViewModel.kt:16`
3. Add a `BottomNavDestination` entry in `core/router/BottomNavGraph.kt:15` **or** a route in
   `core/router/RootNavHost.kt:29` for full-screen destinations
4. If the screen needs a new repository, define the interface in `domain/repository/`, implement
   it in `data/repository/`, and bind them in `di/RepositoryModule.kt`

## Configuration

- **API base URL**: `data/remote/ApiURL.kt`
- **Auth token**: `core/networks/ApiInterceptor.kt:12` (currently placeholder — read from
  secure storage before shipping)
- **Room DB name / version**: `di/PersistenceModule.kt:24` / `data/local/room_db/AppDatabase.kt`
- **Supported locales**: `core/utils/LocaleManager.kt` — add new codes there and in `res/values-*/strings.xml`

## Additional Documentation

| File | When to check |
|------|---------------|
| `.claude/docs/architectural_patterns.md` | Before adding a ViewModel, repository, navigation route, or any new screen — covers BaseViewModel, Clean Architecture layers, mapper convention, Hilt module split, reactive repository patterns, CompositionLocal usage, LoadStatus state machine, and SavedStateHandle |
