# Architectural Patterns

Patterns that appear across multiple files in this codebase.

---

## 1. Generic BaseViewModel with Typed State & Events

Every screen owns a `BaseViewModel<S : UiState, E : UiEvent>` subclass. State is updated
immutably via a DSL reducer; one-time side effects travel through a separate `SharedFlow`.

- Base class: `core/viewmodel/BaseViewModel.kt:16-38`
  - `setState { copy(...) }` — immutable reducer pattern (line 23)
  - `sendEvent(e)` — fire-and-forget on `Main.immediate` (line 34)
- Concrete implementations (all follow the same shape):
  - `ui/pages/diary/DiaryViewModel.kt:15-19` — `@HiltViewModel` + `BaseViewModel<DiaryUiState, DiaryUiEvent>`
  - `ui/pages/diary/diary_detail/DiaryDetailViewModel.kt:16-21`
  - `ui/pages/quote_page/QuoteViewModel.kt`
  - `ui/main/MainViewModel.kt`

**UiState convention** — immutable `data class` with `LoadStatus` and all screen data:
- `ui/pages/diary/DiaryUiState.kt:9-13`
- `ui/pages/quote_page/QuoteUiState.kt:5-9`
- `ui/main/MainUiState.kt:6-8`

**UiEvent convention** — `sealed class` carrying one-time navigation or toast triggers:
- `ui/pages/diary/DiaryUiEvent.kt:5-7`
- `ui/pages/diary/diary_detail/DiaryDetailEvent.kt`

---

## 2. Clean Architecture Layer Separation

Three strict layers; dependencies only point inward (UI → Domain ← Data).

| Layer | Location | Rule |
|-------|----------|------|
| Domain | `domain/entities/`, `domain/repository/` | No Android or framework imports |
| Data | `data/repository/`, `data/local/`, `data/remote/`, `data/mapper/` | Implements domain interfaces |
| UI | `ui/`, `core/` | Consumes domain entities; never touches data models |

- Domain interface example: `domain/repository/DiaryRepository.kt:7-19`
- Data implementation: `data/repository/DiaryRepositoryImpl.kt:12-47` — wraps `DiaryDao`, maps entities via extension functions
- All ViewModels inject the domain *interface*, never the `*Impl` directly (see `DiaryViewModel.kt:17`)

---

## 3. Extension-Function Mapper Pattern

Bidirectional conversion between data-layer models and domain entities via top-level
extension functions grouped in a `mapper/` file. No mapper classes or inheritance needed.

- `data/mapper/DiaryMapper.kt:7-25`
  - `DiaryEntity.toDomain()` — converts Unix timestamp `Int` → `Date` (line 12)
  - `Diary.toEntity()` — converts `Date` → Unix timestamp `Int` (line 22)
- Consumed in `DiaryRepositoryImpl.kt:19-22` — `Flow.map { it.toDomain() }` keeps the stream
  fully typed in domain terms above the repository boundary

Add new mappers in a `*Mapper.kt` file inside `data/mapper/`; use the same `toDomain()` /
`toEntity()` naming convention.

---

## 4. Hilt Dependency Injection — Three-Module Split

All DI lives in `di/`. Modules are split by concern and all installed in `SingletonComponent`.

- **NetworkModule** (`di/NetworkModule.kt:20-113`) — `OkHttpClient`, `Retrofit`, `ApiService`
  - Each dependency is a separate `@Provides @Singleton` function so they can be replaced
    independently in tests
  - Timeout wired at line 61-64 (40 s for all operations)
- **PersistenceModule** (`di/PersistenceModule.kt:14-33`) — `AppDatabase`, `DiaryDao`
- **RepositoryModule** (`di/RepositoryModule.kt:15-37`) — binds `*Impl` to domain interfaces

ViewModels use `@HiltViewModel` + `@Inject constructor`; Compose screens obtain them via
`hiltViewModel<T>()` (e.g. `ui/pages/setting/SettingPage.kt:30`).

---

## 5. Reactive Repository — Flow + Sandwich

Two distinct patterns depending on whether data is local or remote.

**Local (Room → Flow)**
- DAO returns `Flow<List<DiaryEntity>>` (see `data/local/room_db/DiaryDao.kt`)
- Repository pipes it through `Flow.map { it.toDomain() }` — no manual subscription
- `data/repository/DiaryRepositoryImpl.kt:18-23`

**Remote (Retrofit + Sandwich)**
- Repository wraps the call in a `flow { }` builder
- Sandwich's `suspendOnSuccess / onError / onFailure` handles all three result branches
- `onStart` / `onCompletion` callbacks are passed in by the ViewModel for loading state
- `data/repository/RandomQuoteRepositoryImpl.kt:23-33`

---

## 6. Two-Level Navigation Graph

Navigation is split into two composable graphs to isolate bottom-tab routing from
full-screen detail routing.

- **Root graph** (`core/router/RootNavHost.kt:18-40`) — owns `NavHost`; start destination is
  `BOTTOM_NAV`; detail screens are declared here with typed `navArgument`
- **Bottom nav graph** (`core/router/BottomNavGraph.kt:46-68`) — a `NavGraphBuilder` extension
  function; iterates `BottomNavDestination.entries` to register composables
- **Destinations as enums** — `BottomNavDestination` carries route string, label resource,
  icon resource, and content description in one place (`BottomNavGraph.kt:14-44`)
- **Type-safe route helper** — `RootDestination.createDiaryDetailRoute(diaryId)` at
  `RootNavHost.kt:15` avoids stringly-typed navigation call sites
- Bottom bar visibility: `shouldShowBottomNavBar(currentRoute)` at `BottomNavGraph.kt:70`

---

## 7. CompositionLocal for Cross-Screen Shared State

`MainViewModel` (theme + language) is hoisted to `MainActivity` and injected into the
Compose tree via `CompositionLocalProvider`. Child screens read it without prop drilling.

- Provider definition: `global/LocalMainViewModel.kt:8-19`
  - `staticCompositionLocalOf` throws at runtime if not provided — fails fast in previews
- Provision site: `ui/main/MainActivity.kt` (wraps `MainPage` in `ProvideMainViewModel`)
- Consumption: `ui/pages/setting/SettingPage.kt:31` — `LocalMainViewModel.current`

Use this pattern only for truly global state (theme, locale). Per-screen state stays in
each screen's own ViewModel.

---

## 8. LoadStatus Enum for Async State Machine

All async operations progress through a shared four-state enum rather than ad-hoc booleans.

- Enum: `domain/entities/enums/LoadStatus.kt` — `INITIAL | LOADING | SUCCESS | FAILURE`
- Pattern in ViewModel:
  1. `setState { copy(loadStatus = LoadStatus.LOADING) }` before launching
  2. `.collect { ... }` → `LOADING → SUCCESS` (e.g. `DiaryViewModel.kt:27-39`)
  3. `.catch { ... }` → `FAILURE` + `sendEvent(ErrorEvent)` (e.g. `DiaryViewModel.kt:29-32`)
- Also used in `DiaryDetailViewModel.kt:79-106` for save operations

---

## 9. SavedStateHandle for Navigation Argument Injection

Detail ViewModels read route arguments directly from `SavedStateHandle` rather than
parsing `NavBackStackEntry` in the composable.

- `ui/pages/diary/diary_detail/DiaryDetailViewModel.kt:19,24`
  ```
  savedStateHandle.get<Int>("diary_id")
  ```
- The argument name must match the `navArgument` key declared in `RootNavHost.kt:32`
