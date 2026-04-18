# Parallel Implementation Checklists

## TRACK 1: Home & Entry Detail Viewing (Senior Dev A)

### Phase 1: Navigation & Transitions (Days 1-2)
- [ ] Add transition animations to RootNavHost
  - [ ] Slide transition when entering diary detail
  - [ ] Fade transition when exiting
  - [ ] File: `/core/router/RootNavHost.kt`
  
- [ ] Add back button to DiaryDetailPage
  - [ ] Add back navigation button in header
  - [ ] Handle system back press
  - [ ] File: `/ui/pages/diary/diary_detail/DiaryDetailPage.kt`

### Phase 2: Home Screen Enhancements (Days 2-3)
- [ ] Add pull-to-refresh to DiaryPage
  - [ ] Implement refresh logic in DiaryViewModel
  - [ ] Show loading indicator
  - [ ] File: `/ui/pages/diary/DiaryPage.kt`
  
- [ ] Add sorting options
  - [ ] Add sort menu button in top bar
  - [ ] Sort by date (ascending/descending)
  - [ ] Sort by title (A-Z/Z-A)
  - [ ] Add sort state to DiaryUiState
  - [ ] Files: `/ui/pages/diary/DiaryPage.kt`, `DiaryViewModel.kt`, `DiaryUiState.kt`
  
- [ ] Add date range filtering
  - [ ] Add filter button in top bar
  - [ ] Date picker range selection
  - [ ] Update DiaryViewModel.getDiaries() with date params
  - [ ] Files: `/ui/pages/diary/DiaryPage.kt`, `DiaryViewModel.kt`, `DiaryUiState.kt`

- [ ] Add pagination/lazy loading
  - [ ] Implement offset-based pagination
  - [ ] Load more when scrolling to end
  - [ ] Show loading indicator for next page
  - [ ] Files: `/ui/pages/diary/DiaryPage.kt`, `DiaryViewModel.kt`

### Phase 3: Entry Detail Viewing Mode (Days 3-4)
- [ ] Create view-only mode
  - [ ] Add `isViewOnly: Boolean` to DiaryDetailUiState
  - [ ] Hide edit controls when `isViewOnly = true`
  - [ ] Hide save button
  - [ ] Show read-only UI
  - [ ] File: `/ui/pages/diary/diary_detail/DiaryDetailUiState.kt`

- [ ] Add timestamps display
  - [ ] Show "Created: MM/DD/YYYY HH:MM"
  - [ ] Show "Updated: MM/DD/YYYY HH:MM"
  - [ ] Format using SimpleDateFormat
  - [ ] File: `/ui/pages/diary/diary_detail/DiaryDetailPage.kt`

- [ ] Add sharing functionality
  - [ ] Add share button to header
  - [ ] Share entry as text/markdown
  - [ ] Use Intent.ACTION_SEND
  - [ ] File: `/ui/pages/diary/diary_detail/DiaryDetailPage.kt`

- [ ] Add copy-to-clipboard
  - [ ] Add copy button to content area
  - [ ] Show toast on success
  - [ ] Use ClipboardManager
  - [ ] File: `/ui/pages/diary/diary_detail/DiaryDetailPage.kt`

### Phase 4: Delete Workflow Improvements (Days 4)
- [ ] Add undo snackbar after deletion
  - [ ] Show snackbar with "Undo" action
  - [ ] Re-insert diary on undo
  - [ ] Add event handling in DiaryPage
  - [ ] Files: `/ui/pages/diary/DiaryPage.kt`, `DiaryViewModel.kt`

- [ ] Add loading state during deletion
  - [ ] Disable delete button while processing
  - [ ] Show loading indicator
  - [ ] Add `isDeletingId: Int?` to DiaryUiState
  - [ ] Files: `/ui/pages/diary/DiaryPage.kt`, `DiaryViewModel.kt`, `DiaryUiState.kt`

- [ ] Improve error handling
  - [ ] Catch delete errors in ViewModel
  - [ ] Show error snackbar to user
  - [ ] Log errors properly
  - [ ] Files: `/ui/pages/diary/DiaryViewModel.kt`

### Phase 5: Quality & Testing (Day 5)
- [ ] Code review by team
- [ ] Unit tests for ViewModel logic
  - [ ] Search filtering tests
  - [ ] Sort logic tests
  - [ ] Delete operation tests
  - [ ] File: `/app/src/test/`

- [ ] UI tests
  - [ ] Navigation flow tests
  - [ ] Sorting UI tests
  - [ ] Delete dialog tests
  - [ ] File: `/app/src/androidTest/`

- [ ] Manual testing
  - [ ] Test all sorting options
  - [ ] Test date filtering
  - [ ] Test pagination
  - [ ] Test delete workflow
  - [ ] Test sharing
  - [ ] Test undo

---

## TRACK 2: New Entry Form & Calendar (Senior Dev B)

### Phase 1: Calendar ViewModel & State (Days 1-2)
- [ ] Create CalendarViewModel
  - [ ] Extend BaseViewModel<CalendarUiState, CalendarUiEvent>
  - [ ] Inject DiaryRepository
  - [ ] Initialize month navigation state
  - [ ] Load diaries for current month in init block
  - [ ] Create file: `/ui/pages/calendar/CalendarViewModel.kt`

- [ ] Create CalendarUiState
  - [ ] Add `currentMonth: YearMonth` field
  - [ ] Add `diaryDates: Set<LocalDate>` for entry dates
  - [ ] Add `selectedDate: LocalDate?` for selected day
  - [ ] Add `loadStatus: LoadStatus` for loading state
  - [ ] Add `diaryEntriesForDay: List<Diary>` for day view
  - [ ] Create file: `/ui/pages/calendar/CalendarUiState.kt`

- [ ] Create CalendarUiEvent
  - [ ] Add `NavigateToEntry(entryId: Int)` event
  - [ ] Add `NavigateToCreateEntry(date: LocalDate)` event
  - [ ] Add `LoadError(message: String)` event
  - [ ] Create file: `/ui/pages/calendar/CalendarUiEvent.kt`

### Phase 2: Calendar Data Loading (Days 2-3)
- [ ] Implement month data loading
  - [ ] Load all diaries on ViewModel init
  - [ ] Extract dates for current month
  - [ ] Update diaryDates in state
  - [ ] Handle load errors
  - [ ] File: `/ui/pages/calendar/CalendarViewModel.kt`

- [ ] Implement month navigation
  - [ ] Add `nextMonth()` method
  - [ ] Add `previousMonth()` method
  - [ ] Reload diaries when month changes
  - [ ] Update currentMonth in state
  - [ ] File: `/ui/pages/calendar/CalendarViewModel.kt`

- [ ] Implement date selection
  - [ ] Add `selectDate(date: LocalDate)` method
  - [ ] Load entries for selected date
  - [ ] Update selectedDate in state
  - [ ] File: `/ui/pages/calendar/CalendarViewModel.kt`

### Phase 3: Calendar UI Integration (Days 3-4)
- [ ] Update CalendarPage to use ViewModel
  - [ ] Inject CalendarViewModel
  - [ ] Collect state and events
  - [ ] Pass state to calendar grid
  - [ ] File: `/ui/pages/calendar/CalendarPage.kt`

- [ ] Add entry indicators to calendar
  - [ ] Create CalendarDayIndicator component
  - [ ] Show dot/badge on dates with entries
  - [ ] Show entry count if multiple per day
  - [ ] Color code by mood/importance (optional)
  - [ ] Create file: `/ui/pages/calendar/components/CalendarDayIndicator.kt`

- [ ] Add date selection UI
  - [ ] Highlight selected date
  - [ ] Show entry list for selected date
  - [ ] Add day view showing all entries for selected date
  - [ ] File: `/ui/pages/calendar/CalendarPage.kt`

### Phase 4: Calendar Navigation (Days 4)
- [ ] Add date click navigation
  - [ ] Single entry per date -> navigate directly
  - [ ] Multiple entries per date -> show day view picker
  - [ ] Handle navigation events from ViewModel
  - [ ] File: `/ui/pages/calendar/CalendarPage.kt`

- [ ] Add long-press for new entry
  - [ ] Long press date to create new entry
  - [ ] Pass selected date to DiaryDetailPage
  - [ ] Pre-fill createdDate in form
  - [ ] File: `/ui/pages/calendar/CalendarPage.kt`

- [ ] Add create entry via FAB
  - [ ] FAB creates entry for today
  - [ ] Navigate to DiaryDetailPage with today's date
  - [ ] File: `/ui/pages/calendar/CalendarPage.kt`

### Phase 5: Form Validation & Save (Days 4-5)
- [ ] Implement form validation
  - [ ] Add validation error state to DiaryDetailUiState
  - [ ] Validate title is not empty
  - [ ] Validate content is not empty
  - [ ] Show error messages inline
  - [ ] Files: `/ui/pages/diary/diary_detail/DiaryDetailPage.kt`, `DiaryDetailUiState.kt`

- [ ] Implement save workflow
  - [ ] Create proper saveDiary() method in DiaryDetailViewModel
  - [ ] Handle insert vs update logic
  - [ ] Add loading state during save
  - [ ] Send SaveSuccess event on success
  - [ ] Send SaveError event on failure
  - [ ] Files: `/ui/pages/diary/diary_detail/DiaryDetailViewModel.kt`, `DiaryDetailEvent.kt`

- [ ] Add save feedback
  - [ ] Show loading spinner while saving
  - [ ] Show success snackbar after save
  - [ ] Show error snackbar on failure
  - [ ] Navigate back to list on success
  - [ ] File: `/ui/pages/diary/diary_detail/DiaryDetailPage.kt`

- [ ] Implement unsaved changes warning
  - [ ] Track if content changed since load
  - [ ] Warn user before leaving if unsaved
  - [ ] Offer save/discard options
  - [ ] File: `/ui/pages/diary/diary_detail/DiaryDetailPage.kt`

### Phase 6: Form Enhancements (Days 5-6)
- [ ] Add draft saving
  - [ ] Auto-save drafts periodically (e.g., every 30 seconds)
  - [ ] Save to separate draft table in Room DB
  - [ ] Load draft on new entry page if exists
  - [ ] Add recover draft option
  - [ ] File: `/ui/pages/diary/diary_detail/DiaryDetailViewModel.kt`

- [ ] Enhance rich text editor
  - [ ] Add formatting toolbar (bold, italic, etc.)
  - [ ] Add heading levels
  - [ ] Add bullet lists
  - [ ] File: `/ui/pages/diary/diary_detail/DiaryDetailPage.kt`

- [ ] Improve image handling
  - [ ] Add image preview gallery
  - [ ] Add image deletion per image
  - [ ] Add drag-drop image reordering
  - [ ] Add image compression before save
  - [ ] File: `/ui/pages/diary/diary_detail/DiaryDetailPage.kt`

- [ ] Enhance location feature
  - [ ] Add map view of location
  - [ ] Add manual location picker
  - [ ] Store location history
  - [ ] Show recent locations as suggestions
  - [ ] File: `/ui/pages/diary/diary_detail/DiaryDetailPage.kt`

### Phase 7: Quality & Testing (Days 6-7)
- [ ] Unit tests for CalendarViewModel
  - [ ] Test month navigation
  - [ ] Test data loading
  - [ ] Test date filtering
  - [ ] File: `/app/src/test/`

- [ ] Unit tests for form validation
  - [ ] Test validation logic
  - [ ] Test save vs update logic
  - [ ] File: `/app/src/test/`

- [ ] UI tests
  - [ ] Test calendar date selection
  - [ ] Test entry creation flow
  - [ ] Test form validation display
  - [ ] Test save success/error states
  - [ ] File: `/app/src/androidTest/`

- [ ] Manual testing
  - [ ] Test calendar month navigation
  - [ ] Test entry indicators display
  - [ ] Test form validation
  - [ ] Test save and navigation back
  - [ ] Test draft recovery
  - [ ] Test image insertion and deletion
  - [ ] Test location insertion

---

## SHARED INTEGRATION TASKS

### Initial Setup (Day 1)
- [ ] Both tracks review architectural patterns
  - [ ] Review `/CLAUDE.md` section on patterns
  - [ ] Review BaseViewModel pattern
  - [ ] Review repository pattern
  - [ ] Review navigation patterns

- [ ] Verify build configuration
  - [ ] Run `./gradlew clean build`
  - [ ] Verify no build errors
  - [ ] Check lint passes

### Daily Sync Points
- [ ] 10 AM: Team standup
  - Track 1 updates on progress
  - Track 2 updates on progress
  - Discuss blockers
  - Coordinate integration points

- [ ] 3 PM: Integration check
  - Verify no merge conflicts
  - Test cross-feature navigation
  - Verify Material 3 consistency

### Final Integration (Last 2 days)
- [ ] Merge Track 1 and Track 2
  - [ ] Resolve any conflicts
  - [ ] Test full app flow
  - [ ] Verify all navigation works

- [ ] Cross-feature testing
  - [ ] Diary list → Detail → Diary list navigation
  - [ ] Calendar → Create Entry → Diary Detail flow
  - [ ] Calendar → Entry Detail → Back to calendar flow

- [ ] Combined quality check
  - [ ] All tests passing
  - [ ] No lint warnings
  - [ ] Material 3 design consistency
  - [ ] Performance acceptable

---

## CODE STRUCTURE REFERENCE

### Track 1 Creates/Modifies
```
Created or Enhanced:
- /ui/pages/diary/DiaryPage.kt (enhancements)
- /ui/pages/diary/DiaryViewModel.kt (enhancements)
- /ui/pages/diary/DiaryUiState.kt (enhancements)
- /ui/pages/diary/diary_detail/DiaryDetailPage.kt (enhancements)
- /ui/pages/diary/diary_detail/DiaryDetailViewModel.kt (minor)
- /ui/pages/diary/diary_detail/DiaryDetailUiState.kt (enhancements)
- /core/router/RootNavHost.kt (enhancements)
```

### Track 2 Creates/Modifies
```
Created:
- /ui/pages/calendar/CalendarViewModel.kt (NEW)
- /ui/pages/calendar/CalendarUiState.kt (NEW)
- /ui/pages/calendar/CalendarUiEvent.kt (NEW)
- /ui/pages/calendar/components/CalendarDayIndicator.kt (NEW)

Enhanced:
- /ui/pages/calendar/CalendarPage.kt (enhancements)
- /ui/pages/diary/diary_detail/DiaryDetailPage.kt (enhancements)
- /ui/pages/diary/diary_detail/DiaryDetailViewModel.kt (enhancements)
- /ui/pages/diary/diary_detail/DiaryDetailUiState.kt (enhancements)
- /ui/pages/diary/diary_detail/DiaryDetailEvent.kt (enhancements)
```

---

## Key Dependencies & Imports

### Track 1 Common Imports
```kotlin
import androidx.navigation.NavHostController
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material.icons.filled.*
import java.text.SimpleDateFormat
import android.content.ClipboardManager
```

### Track 2 Common Imports
```kotlin
import java.time.YearMonth
import java.time.LocalDate
import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.launch
```

---

## Definition of Done for Each Track

### Track 1 Definition of Done
- [ ] All SOLID principles followed
- [ ] No lint errors or warnings
- [ ] Material 3 design applied consistently
- [ ] Navigation animations implemented
- [ ] All features have error handling
- [ ] Snackbars/toasts for user feedback
- [ ] Tests pass (unit + UI)
- [ ] Manual testing completed successfully
- [ ] Code reviewed and approved
- [ ] Ready for merge to main

### Track 2 Definition of Done
- [ ] All SOLID principles followed
- [ ] No lint errors or warnings
- [ ] Material 3 design applied consistently
- [ ] Calendar UI responsive and performant
- [ ] Form validation working correctly
- [ ] Save workflow complete
- [ ] Navigation events handled properly
- [ ] Tests pass (unit + UI)
- [ ] Manual testing completed successfully
- [ ] Code reviewed and approved
- [ ] Ready for merge to main

