package wade.owen.watts.base_jetpack.ui.pages.diary.diary_detail

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import wade.owen.watts.base_jetpack.core.viewmodel.BaseViewModel
import wade.owen.watts.base_jetpack.domain.entities.Diary
import wade.owen.watts.base_jetpack.domain.entities.enums.LoadStatus
import wade.owen.watts.base_jetpack.domain.repository.DiaryRepository
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@HiltViewModel
class DiaryDetailViewModel @Inject constructor(
    private val diaryRepository: DiaryRepository,
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<DiaryDetailUiState, DiaryDetailEvent>(
    initialState = DiaryDetailUiState()
) {
    private var autoSaveJob: kotlinx.coroutines.Job? = null
    private var lastSavedContent: Pair<String, String>? = null  // (title, content)

    init {
        val diaryId: Int? = savedStateHandle.get<Int>("diary_id")
        if (diaryId != null && diaryId != -1) {
            loadDiary(diaryId)
        }
        startAutoSave()
    }

    override fun onCleared() {
        autoSaveJob?.cancel()
        super.onCleared()
    }

    // ── Load ──────────────────────────────────────────────────────────────────

    private fun loadDiary(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            diaryRepository.getDiaryById(id).collect { diary ->
                setState {
                    copy(
                        title = diary.title,
                        content = diary.content,
                        diaryId = diary.id,
                        originalDiary = diary,
                        wordCount = countWords(diary.content),
                        isViewMode = true,
                    )
                }
            }
        }
    }

    // ── Title / Content ───────────────────────────────────────────────────────

    fun updateTitle(title: String) {
        setState { copy(title = title) }
    }

    /** Gọi mỗi khi RichTextState thay đổi — truyền plain text để lưu & đếm từ. */
    fun updateContent(plain: String) {
        setState { copy(content = plain, wordCount = countWords(plain)) }
    }

    private fun countWords(text: String): Int =
        text.trim().split(Regex("\\s+")).count { it.isNotEmpty() }

    // ── Image ─────────────────────────────────────────────────────────────────

    fun addImage(uri: Uri) {
        setState { copy(imageUris = imageUris + uri) }
    }

    fun removeImage(uri: Uri) {
        setState { copy(imageUris = imageUris - uri) }
    }

    // ── Location ─────────────────────────────────────────────────────────────

    @SuppressLint("MissingPermission")
    fun fetchAndInsertLocation() {
        viewModelScope.launch {
            setState { copy(isLoadingLocation = true) }
            try {
                val fusedClient =
                    LocationServices.getFusedLocationProviderClient(context)
                val cancelSrc = CancellationTokenSource()

                val location = withContext(Dispatchers.IO) {
                    suspendCancellableCoroutine { cont ->
                        fusedClient
                            .getCurrentLocation(
                                Priority.PRIORITY_HIGH_ACCURACY,
                                cancelSrc.token
                            )
                            .addOnSuccessListener { loc -> cont.resume(loc) }
                            .addOnFailureListener { e ->
                                cont.resumeWithException(
                                    e
                                )
                            }
                        cont.invokeOnCancellation { cancelSrc.cancel() }
                    }
                }

                val address = location?.let {
                    reverseGeocode(it.latitude, it.longitude)
                } ?: "Unknown location"

                setState {
                    copy(
                        isLoadingLocation = false,
                        locationText = address,
                        content = if (content.isBlank()) "📍 $address"
                        else "$content\n\n📍 $address",
                    )
                }
                sendEvent(DiaryDetailEvent.LocationInserted(address))
            } catch (e: Exception) {
                Log.e("DiaryDetailVM", "fetchLocation failed", e)
                setState { copy(isLoadingLocation = false) }
                sendEvent(DiaryDetailEvent.DiaryDetailError("Could not get location"))
            }
        }
    }

    private suspend fun reverseGeocode(lat: Double, lng: Double): String =
        withContext(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    suspendCancellableCoroutine { cont ->
                        geocoder.getFromLocation(lat, lng, 1) { list ->
                            val addr = list.firstOrNull()
                            cont.resume(
                                addr?.let { buildAddress(it) } ?: "$lat, $lng"
                            )
                        }
                    }
                } else {
                    @Suppress("DEPRECATION")
                    val list = geocoder.getFromLocation(lat, lng, 1)
                    val addr = list?.firstOrNull()
                    addr?.let { buildAddress(it) } ?: "$lat, $lng"
                }
            } catch (e: Exception) {
                "$lat, $lng"
            }
        }

    private fun buildAddress(addr: android.location.Address): String {
        val parts = listOfNotNull(
            addr.thoroughfare,
            addr.subLocality ?: addr.locality,
            addr.adminArea,
            addr.countryName,
        )
        return parts.joinToString(", ")
    }

    // ── TTS — disabled, implement later ──────────────────────────────────────
    fun onTtsClick() {
        // TODO: implement Text-to-Speech
    }

    // ── Validation ────────────────────────────────────────────────────────────

    companion object {
        private const val TITLE_MIN_CHARS = 1
        private const val TITLE_MAX_CHARS = 200
        private const val CONTENT_CHAR_WARNING = 5000
    }

    fun validateEntry(): Boolean {
        val s = state.value

        // Title validation: required, min 1 char, max 200 chars
        if (s.title.isBlank()) {
            setState { copy(validationError = "Title is required") }
            return false
        }

        if (s.title.length < TITLE_MIN_CHARS) {
            setState { copy(validationError = "Title must be at least $TITLE_MIN_CHARS character") }
            return false
        }

        if (s.title.length > TITLE_MAX_CHARS) {
            setState { copy(validationError = "Title cannot exceed $TITLE_MAX_CHARS characters (current: ${s.title.length})" ) }
            return false
        }

        // Clear validation error on success
        setState { copy(validationError = null) }
        return true
    }

    fun getValidationError(): String? = state.value.validationError

    fun clearValidationError() {
        setState { copy(validationError = null) }
    }

    fun switchToEditMode() {
        setState { copy(isViewMode = false) }
    }

    // ── Auto-Save (Optional Enhancement) ──────────────────────────────────────

    private fun startAutoSave() {
        autoSaveJob?.cancel()
        autoSaveJob = viewModelScope.launch {
            while (true) {
                delay(30000) // Auto-save every 30 seconds
                val s = state.value
                val currentContent = Pair(s.title, s.content)

                // Only save if content changed and is not empty
                if (currentContent != lastSavedContent && (s.title.isNotEmpty() || s.content.isNotEmpty())) {
                    autoSaveDraft()
                }
            }
        }
    }

    private suspend fun autoSaveDraft() {
        setState { copy(isSavingDraft = true) }
        val s = state.value
        try {
            // Only auto-save if it's an existing entry or has meaningful content
            if (s.originalDiary != null && (s.title != s.originalDiary.title || s.content != s.originalDiary.content)) {
                withContext(Dispatchers.IO) {
                    diaryRepository.updateDiary(
                        s.originalDiary.copy(
                            title = s.title,
                            content = s.content,
                            updatedDate = Date(),
                        )
                    )
                }
                lastSavedContent = Pair(s.title, s.content)
                setState { copy(draftSavedIndicator = "Draft saved", isSavingDraft = false) }

                // Clear the indicator after 2 seconds
                delay(2000)
                setState { copy(draftSavedIndicator = null) }
            } else {
                setState { copy(isSavingDraft = false) }
            }
        } catch (e: Exception) {
            Log.e("DiaryDetailVM", "autoSaveDraft failed", e)
            setState { copy(isSavingDraft = false) }
        }
    }

    fun checkChangesAndDismiss() {
        val s = state.value
        val hasChanges = if (s.originalDiary != null) {
            s.title != s.originalDiary.title || s.content != s.originalDiary.content
        } else {
            s.title.isNotEmpty() || s.content.isNotEmpty() || s.imageUris.isNotEmpty()
        }
        if (hasChanges) setState { copy(showDiscardDialog = true) }
        else sendEvent(DiaryDetailEvent.NavigateBack)
    }

    fun dismissDiscardDialog() {
        setState { copy(showDiscardDialog = false) }
    }

    fun confirmDiscard() {
        sendEvent(DiaryDetailEvent.NavigateBack)
    }

    fun saveDiary() {
        // Validate before saving
        if (!validateEntry()) {
            sendEvent(DiaryDetailEvent.ValidationError(getValidationError() ?: "Validation failed"))
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            setState { copy(loadStatus = LoadStatus.LOADING, isSavingDraft = true) }
            val s = state.value
            try {
                if (s.originalDiary != null) {
                    diaryRepository.updateDiary(
                        s.originalDiary.copy(
                            title = s.title,
                            content = s.content,
                            updatedDate = Date(),
                        )
                    )
                } else {
                    diaryRepository.insertDiary(
                        Diary(
                            title = s.title,
                            content = s.content,
                            createdDate = Date(),
                            updatedDate = Date(),
                        )
                    )
                }
                setState { copy(loadStatus = LoadStatus.SUCCESS, isSavingDraft = false) }
                sendEvent(DiaryDetailEvent.SaveSuccess)
                sendEvent(DiaryDetailEvent.NavigateBack)
            } catch (e: Exception) {
                Log.e("DiaryDetailVM", "saveDiary failed", e)
                setState { copy(loadStatus = LoadStatus.FAILURE, isSavingDraft = false) }
                sendEvent(DiaryDetailEvent.DiaryDetailError("Save diary failed"))
            }
        }
    }

    // ── Delete with Undo ──────────────────────────────────────────────────────

    private var deletedDiary: Diary? = null
    private var undoJob: kotlinx.coroutines.Job? = null

    fun showDeleteConfirmDialog() {
        setState { copy(showDeleteConfirmDialog = true) }
    }

    fun dismissDeleteConfirmDialog() {
        setState { copy(showDeleteConfirmDialog = false) }
    }

    fun confirmDelete() {
        dismissDeleteConfirmDialog()
        val s = state.value
        val diary = s.originalDiary ?: return
        deletedDiary = diary
        viewModelScope.launch(Dispatchers.IO) {
            setState { copy(isDeletingEntry = true) }
            try {
                diaryRepository.deleteDiary(diary)
                setState { copy(isDeletingEntry = false) }
                sendEvent(
                    DiaryDetailEvent.ShowUndoSnackbar(
                        "Entry deleted. Undo in 3 seconds..."
                    )
                )
                // Schedule permanent deletion after 3 seconds if not undone
                undoJob?.cancel()
                undoJob = viewModelScope.launch {
                    delay(3000)
                    deletedDiary = null // Clear after timeout
                }
            } catch (e: Exception) {
                Log.e("DiaryDetailVM", "deleteDiary failed", e)
                deletedDiary = null
                setState { copy(isDeletingEntry = false) }
                sendEvent(DiaryDetailEvent.DiaryDetailError("Delete failed"))
            }
        }
    }

    fun undoDelete() {
        val diary = deletedDiary ?: return
        undoJob?.cancel()
        viewModelScope.launch(Dispatchers.IO) {
            setState { copy(isDeletingEntry = true) }
            try {
                diaryRepository.insertDiary(diary)
                setState {
                    copy(
                        isDeletingEntry = false,
                        originalDiary = diary,
                        title = diary.title,
                        content = diary.content,
                    )
                }
                sendEvent(DiaryDetailEvent.DiaryDetailError("Entry restored"))
                deletedDiary = null
            } catch (e: Exception) {
                Log.e("DiaryDetailVM", "undoDelete failed", e)
                setState { copy(isDeletingEntry = false) }
                sendEvent(DiaryDetailEvent.DiaryDetailError("Undo failed"))
            }
        }
    }
}
