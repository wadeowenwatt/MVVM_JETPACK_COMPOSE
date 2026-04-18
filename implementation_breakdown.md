# Jetpack Compose Diary App - Implementation Breakdown

## Screens Designed (Light + Dark):
1. **Home (Diary)**: Displays diary entries list with cards, status bar, search button
2. **New Entry (Diary Detail)**: Form for creating diary entries with rich text
3. **Entry Detail**: View detailed entry with rich content
4. **Calendar**: Monthly calendar view with entry indicators
5. **Profile/Settings**: App settings (theme, locale, etc.)

## Current Codebase State:
- Clean Architecture fully implemented (Domain → Data → UI)
- MVVM pattern with `BaseViewModel<S, E>` established
- Material 3 design system configured
- Jetpack Navigation Compose with RootNavHost + BottomNavGraph
- Room database with full persistence layer
- Hilt dependency injection all wired up
- **Screens already partially/fully implemented**:
  - DiaryPage (Home) ✓ - List display with search
  - DiaryDetailPage (New/Edit Entry) ✓ - Rich text editor, images, location
  - CalendarPage ✓ - Basic calendar UI
  - QuotePage ✓ - API integration demo
  - SettingPage ✓ - Theme/locale settings

---

# Implementation Task Breakdown (Parallel Tracks)

## TRACK 1: Home & Entry Detail (Senior Dev A)

### Objective
Implement complete diary list display, search, deletion, and detailed entry viewing with full navigation integration.

### 1. Navigation Enhancement
**Files to modify:**
- `/core/router/RootNavHost.kt`
- `/core/router/BottomNavGraph.kt`

**Tasks:**
- [x] DIARY detail route already defined (`diary_detail/{diary_id}`)
- [x] Route creation helper already implemented (`createDiaryDetailRoute()`)
- [x] Bottom nav graph already configured
- [ ] **TODO**: Add support for nested navigation if needed for entry viewing
- [ ] **TODO**: Add transition animations between diary list ↔ detail

**Code patterns to follow:**
```kotlin
// In RootDestination object:
// Already has: const val DIARY_DETAIL = "diary_detail/{diary_id}"
// Already has: fun createDiaryDetailRoute(diaryId: Int = -1) = "diary_detail/$diaryId"
```

---

### 2. Home Screen (DiaryPage) - Enhancements
**File:** `/ui/pages/diary/DiaryPage.kt`
**Current State:** ✓ Already fully implemented with:
- Search functionality (expandable search bar)
- Diary card list display
- Delete dialog confirmation
- FAB for creating new entries
- Empty state handling

**Remaining enhancements:**
- [ ] Add swipe-to-delete gesture
- [ ] Add sorting options (date, title)
- [ ] Add filtering by date range
- [ ] Add pull-to-refresh
- [ ] Add pagination/lazy loading for large lists

---

### 3. Diary List ViewModel
**File:** `/ui/pages/diary/DiaryViewModel.kt`
**Current State:** ✓ Already fully implemented with:
- `observeListDiary()` - Loads diaries from repository
- `toggleSearch()` / `updateSearchQuery()` - Search state management
- `showDeleteDialog()` / `dismissDeleteDialog()` - Delete confirmation
- `deleteDiary()` - Deletes entry

**UiState:** `/ui/pages/diary/DiaryUiState.kt`
**Current State:** ✓ Already has:
```kotlin
data class DiaryUiState(
    val loadStatus: LoadStatus = LoadStatus.INITIAL,
    val diaries: List<Diary> = emptyList(),
    val diaryPendingToDelete: Diary? = null,
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
) : UiState {
    val filteredDiaries: List<Diary> // computed
}
```

**UiEvent:** `/ui/pages/diary/DiaryUiEvent.kt`
**Current State:** ✓ Already has deletion and error events

---

### 4. Entry Detail Screen (View-Only Mode)
**File:** `/ui/pages/diary/diary_detail/DiaryDetailPage.kt`
**Current State:** ✓ Already fully implemented with:
- Rich text editing (via `rememberRichTextState()`)
- Image insertion and gallery
- Location insertion with geocoding
- Word count display
- Word recording functionality
- Markdown support

**Viewing mode enhancements needed:**
- [ ] **Add read-only view mode** when `isNew == false`
- [ ] Hide edit controls and save button in view-only mode
- [ ] Add "Edit" button to switch to edit mode
- [ ] Add back navigation button
- [ ] Show created/updated timestamps
- [ ] Add sharing functionality
- [ ] Add copy-to-clipboard for content

**ViewModel:** `/ui/pages/diary/diary_detail/DiaryDetailViewModel.kt`
**Current State:** ✓ Already has:
- `loadDiary(id)` - Loads entry by ID
- `updateTitle()` / `updateContent()` - Edits
- `addImage()` / `removeImage()` - Image management
- `insertLocation()` - Location insertion
- Save and update methods

**UiState:** `/ui/pages/diary/diary_detail/DiaryDetailUiState.kt`
**Enhancement needed:**
- [ ] Add `isViewOnly: Boolean` flag to control UI mode
- [ ] Add `showEditButton: Boolean` to show edit option
- [ ] Add `savedSuccessfully: Boolean` for feedback

---

### 5. List to Detail Navigation Flow
**Implementation:**
- [x] DiaryPage FAB navigates to new entry: `navController.navigate(RootDestination.createDiaryDetailRoute())`
- [x] DiaryCard onClick navigates to edit: `navController.navigate(RootDestination.createDiaryDetailRoute(diary.id))`
- [ ] **TODO**: Add transition animations
- [ ] **TODO**: Add back button in detail view

**Navigation code pattern:**
```kotlin
// Already implemented in DiaryPage.kt lines 92-94 and 132-134
FloatingActionButton(
    onClick = { navController.navigate(RootDestination.createDiaryDetailRoute()) }
)
// And in DiaryCard:
onEditClick = {
    navController.navigate(
        RootDestination.createDiaryDetailRoute(diary.id)
    )
}
```

---

### 6. Entry Deletion Workflow
**Current Implementation:**
- [x] DiaryCard has delete button → `onDeleteClick`
- [x] DiaryPage shows delete confirmation dialog
- [x] ViewModel has `deleteDiary()` method in `DiaryDetailViewModel`

**Tasks:**
- [x] Confirm deletion flow is complete
- [ ] Add undo snackbar after deletion
- [ ] Add loading state during deletion
- [ ] Add error handling with user feedback

---

## TRACK 2: New Entry Form & Calendar (Senior Dev B)

### Objective
Implement complete entry creation workflow with rich form, calendar view integration, and entry indicators on calendar.

### 1. New Entry Form Screen (Diary Detail in Create Mode)
**File:** `/ui/pages/diary/diary_detail/DiaryDetailPage.kt`
**Current State:** ✓ Already fully implemented with:
- Rich text editor (via `com.mohamedrejeb.richeditor`)
- Title input field
- Image picker with multi-image support
- Location insertion with geocoding
- Word counter
- Voice recording (play button visible)
- Timestamp display

**Form submission workflow:**
- [ ] **TODO**: Implement form validation:
  - Title required (non-empty)
  - Content required (non-empty)
  - Images optional
  - Location optional
- [ ] **TODO**: Show validation errors on form
- [ ] **TODO**: Implement "Save Draft" functionality
- [ ] **TODO**: Add unsaved changes warning before leaving page

**ViewModel:** `/ui/pages/diary/diary_detail/DiaryDetailViewModel.kt`
**Current State:** ✓ Already has save/update methods
**Enhancement:**
- [ ] Add form validation logic
- [ ] Add draft saving
- [ ] Add conflict detection on update
- [ ] Improve error handling

---

### 2. Form Components Breakdown
**Rich Text Editor:**
- [x] Already integrated via `com.mohamedrejeb.richeditor:richeditor-compose`
- [x] Supports markdown, bold, italic, links
- [ ] TODO: Add formatting toolbar customization
- [ ] TODO: Add image insertion within text

**Image Picker:**
- [x] Already implemented with `ActivityResultContracts.PickMultipleVisualMedia()`
- [x] Permission handling with `rememberMultiplePermissionsState()`
- [ ] TODO: Add image preview/gallery
- [ ] TODO: Add image deletion
- [ ] TODO: Add image reordering (drag-drop)

**Location Insertion:**
- [x] Already implemented with Geocoder for reverse geocoding
- [x] Async location fetching
- [ ] TODO: Add map view option
- [ ] TODO: Add location picker
- [ ] TODO: Add location history

**Voice Recording:**
- [ ] TODO: Implement voice recording UI
- [ ] TODO: Implement voice playback
- [ ] TODO: Convert speech-to-text integration
- [ ] TODO: Store audio files

---

### 3. Entry Creation Workflow
**Navigation:**
- [x] FAB in DiaryPage → `navController.navigate(RootDestination.createDiaryDetailRoute())`
  - This creates entry with `diaryId = -1` (new entry mode)
- [x] ViewModel loads entry if `diaryId != -1`
- [ ] **TODO**: Add success navigation back to list after save

**Save Flow:**
```kotlin
// Needed in DiaryDetailViewModel:
fun saveDiary() {
    val diary = Diary(
        id = diaryId ?: 0,
        title = title,
        content = content,
        createdDate = originalDiary?.createdDate ?: Date(),
        updatedDate = Date()
    )
    
    viewModelScope.launch {
        if (diaryId == null || diaryId == -1) {
            diaryRepository.insertDiary(diary)
        } else {
            diaryRepository.updateDiary(diary)
        }
        // Navigate back
        sendEvent(DiaryDetailEvent.SaveSuccess)
    }
}
```

- [ ] **TODO**: Implement this save flow
- [ ] **TODO**: Add loading state during save
- [ ] **TODO**: Handle errors with user messaging
- [ ] **TODO**: Navigate back on success

---

### 4. Calendar Screen
**File:** `/ui/pages/calendar/CalendarPage.kt`
**Current State:** ✓ Basic calendar grid UI implemented with:
- Month/year navigation arrows
- Day grid (7 columns)
- Current date highlighting
- Day selection

**Current limitations:**
- No data integration
- No entry indicators
- No click-to-view functionality
- No ViewModel integration

**Implementation tasks:**
- [ ] **Create CalendarViewModel** (extend `BaseViewModel<CalendarUiState, CalendarUiEvent>`)
  - Load diary dates for current month
  - Track selected date
  - Handle month navigation
  
- [ ] **Create CalendarUiState** with:
  ```kotlin
  data class CalendarUiState(
      val currentMonth: YearMonth,
      val diaryDates: Set<LocalDate>, // dates with entries
      val selectedDate: LocalDate?,
      val loadStatus: LoadStatus = LoadStatus.INITIAL
  ) : UiState
  ```
  
- [ ] **Create CalendarUiEvent** with:
  ```kotlin
  sealed class CalendarUiEvent : UiEvent {
      data class NavigateToEntry(val date: LocalDate) : CalendarUiEvent()
      data class LoadError(val message: String) : CalendarUiEvent()
  }
  ```

- [ ] **Add entry indicators:**
  - Show dot/badge on dates with entries
  - Color code by importance/mood (optional)
  - Show entry count if multiple per day

- [ ] **Add date selection:**
  - Click date to view entries for that day
  - Show entry preview/list for selected date
  - Navigate to entry detail on tap

- [ ] **Add month navigation:**
  - Update diary dates when month changes
  - Load data in `ViewModel.init {}`

- [ ] **Link to entry creation:**
  - Long-press date to create new entry for that date
  - FAB creates entry for today
  - Pre-fill created date on entry detail page

---

### 5. Calendar Data Integration
**Repository interface already defined:**
```kotlin
fun getDiaryByDate(createdDate: Date): Flow<Diary>
fun getDiaries(limit: Int, offset: Int): Flow<List<Diary>>
```

**Needed ViewModel method:**
```kotlin
// In CalendarViewModel
fun loadDiariesForMonth(year: Int, month: Int) {
    viewModelScope.launch(Dispatchers.IO) {
        diaryRepository.getDiaries(Int.MAX_VALUE, 0)
            .map { diaries ->
                diaries.filter { 
                    val cal = Calendar.getInstance().apply { time = it.createdDate }
                    cal.get(Calendar.YEAR) == year && 
                    cal.get(Calendar.MONTH) == month
                }
                .map { it.createdDate.toLocalDate() }
                .toSet()
            }
            .collect { dates ->
                setState { copy(diaryDates = dates, loadStatus = LoadStatus.SUCCESS) }
            }
    }
}
```

---

### 6. Calendar-to-Detail Navigation
**Implementation:**
- [ ] Date click navigates to entry detail with date-filtered view
- [ ] Or shows day view with all entries for that date
- [ ] Implement two approaches:
  1. **Day View**: Show all entries for selected date, let user pick one
  2. **Direct Navigation**: If one entry per date, navigate directly to it

**Navigation route options:**
```kotlin
// Option 1: Add date parameter to existing route
const val DIARY_DETAIL_BY_DATE = "diary_detail_by_date/{date}"

// Option 2: Add new route for day view
const val DIARY_DAY_VIEW = "diary_day_view/{date}"
```

---

## SHARED TASKS (Both Teams)

### 1. UI State Management Patterns
**Already established in codebase:**
```kotlin
// Base pattern
abstract class BaseViewModel<S : UiState, E : UiEvent>(initialState: S) : ViewModel()

// State reduction
protected fun setState(reducer: S.() -> S) {
    _state.value = _state.value.reducer()
}

// Event emission
protected fun sendEvent(event: E) {
    viewModelScope.launch(Dispatchers.Main.immediate) {
        _event.emit(event)
    }
}
```
**Teams must follow this pattern consistently.**

---

### 2. Mapper Pattern (Data → UI)
**Location:** `/ui/mapper/`
**Pattern established:**
```kotlin
// Example in SampleUiMapper.kt
fun Diary.toUiModel() = DiaryUiModel(...)
```
**Teams should create mappers if UI models differ from domain models.**

---

### 3. Material 3 Design System
**Already configured:**
- Theme file: `/ui/theme/Theme.kt`
- Colors: `/ui/theme/Color.kt`
- Typography: `/ui/theme/Type.kt`
- Reusable components:
  - `AppButton.kt`
  - `AppHeader.kt`
  - `AppDialog.kt` (used for delete confirmation)

**Teams must use these components for consistency.**

---

### 4. Dependency Injection
**Hilt modules already configured:**
- `NetworkModule.kt` - API/HTTP
- `PersistenceModule.kt` - Room DB
- `RepositoryModule.kt` - Repository bindings

**No additional DI setup needed for these features.**

---

### 5. Coroutines & Flow Pattern
**Already established:**
- `viewModelScope` for lifecycle-aware coroutines
- `Dispatchers.IO` for DB operations
- `Flow<T>` for reactive data
- Error handling with `.catch {}` blocks

**Teams must follow these patterns.**

---

## File Structure Summary

### Track 1 Files (Home & Entry Detail)
```
app/src/main/java/wade/owen/watts/base_jetpack/
├── core/router/
│   ├── RootNavHost.kt          [MODIFY - Add transitions]
│   └── BottomNavGraph.kt        [NO CHANGE NEEDED]
├── ui/pages/diary/
│   ├── DiaryPage.kt             [ENHANCE - Add swipe, sorting, pagination]
│   ├── DiaryViewModel.kt        [ENHANCE - Add new methods]
│   ├── DiaryUiState.kt          [ENHANCE - Add state for new features]
│   ├── DiaryUiEvent.kt          [CHECK - Review events]
│   └── diary_detail/
│       ├── DiaryDetailPage.kt   [ENHANCE - Add view-only mode]
│       ├── DiaryDetailViewModel.kt [ENHANCE - Add validation, save flow]
│       ├── DiaryDetailUiState.kt   [ENHANCE - Add isViewOnly flag]
│       └── DiaryDetailEvent.kt     [ENHANCE - Add view mode events]
```

### Track 2 Files (New Entry & Calendar)
```
app/src/main/java/wade/owen/watts/base_jetpack/
├── ui/pages/
│   ├── calendar/
│   │   ├── CalendarPage.kt              [ENHANCE - Add data integration]
│   │   ├── CalendarViewModel.kt         [CREATE NEW]
│   │   ├── CalendarUiState.kt           [CREATE NEW]
│   │   ├── CalendarUiEvent.kt           [CREATE NEW]
│   │   └── components/
│   │       └── CalendarDayIndicator.kt  [CREATE NEW - for entry dots]
│   └── diary/
│       └── diary_detail/
│           ├── DiaryDetailPage.kt       [ENHANCE - Form validation, drafts]
│           ├── DiaryDetailViewModel.kt  [ENHANCE - Save flow, validation]
│           ├── DiaryDetailUiState.kt    [ENHANCE - Add validation errors]
│           └── DiaryDetailEvent.kt      [ENHANCE - Add save events]
```

---

## Integration Points

### Database Layer (Already Complete)
- Room DAOs: `/data/local/room_db/`
- Entities: `/data/models/entity/`
- Repository implementation: `/data/repository/`

### Domain Layer (Already Complete)
- Diary entity: `/domain/entities/Diary.kt`
- Repository interface: `/domain/repository/DiaryRepository.kt`
- Enums (LoadStatus, AppTheme): `/domain/entities/enums/`

### Navigation (Partially Complete)
- RootNavHost: `/core/router/RootNavHost.kt` - diary_detail route defined
- BottomNavGraph: `/core/router/BottomNavGraph.kt` - all destinations defined
- SavedStateHandle integration: Already in DiaryDetailViewModel

---

## Testing Considerations

### Track 1 Tests
- List display filtering
- Delete dialog confirmation
- Navigation between list and detail
- Search functionality
- Empty state handling

### Track 2 Tests
- Form validation
- Calendar month navigation
- Date selection
- Entry creation with various input combinations
- Image/location insertion

### UI Tests
- Search bar animations
- Dialog interactions
- Navigation transitions
- Keyboard management

---

## Performance Considerations

### Track 1
- Lazy loading for large diary lists (use `LazyColumn` pagination)
- Image loading optimization (use Coil with caching)
- Search debouncing to avoid excessive filtering

### Track 2
- Calendar month loading optimization (batch load diaries)
- Image compression before storage
- Location geocoding caching

---

## Deployment Checklist

### Before PR Submission
- [ ] All unit tests passing
- [ ] All instrumented tests passing
- [ ] No lint warnings
- [ ] Code follows Kotlin style guide
- [ ] All TODO comments removed or addressed
- [ ] Proguard rules updated if needed
- [ ] Strings externalized to `res/values/strings.xml`
- [ ] Accessibility labels added to all interactive elements

### PR Review Criteria
- Design consistency with Material 3
- MVVM pattern compliance
- Repository pattern usage
- Coroutine/Flow best practices
- Error handling completeness
- Navigation flow correctness
- User feedback for all async operations

