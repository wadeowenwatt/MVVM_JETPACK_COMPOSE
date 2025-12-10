package wade.owen.watts.base_jetpack.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object SharedPrefs {
    private lateinit var prefs: SharedPreferences

    private const val PREF_NAME = "wade.owen.watts.base_jetpack"

    private const val LANGUAGE_CODE_KEY = "language_code"

    fun init(context: Context) {
        prefs = context.getSharedPreferences(
            PREF_NAME,
            Context.MODE_PRIVATE
        )
    }

    fun setLanguageCode(value: String) {
        prefs.edit { putString(LANGUAGE_CODE_KEY, value) }
    }

    fun getLanguageCode(): String? {
        return prefs.getString(LANGUAGE_CODE_KEY, null)
    }

    fun deleteLanguageCode() {
        prefs.edit { remove(LANGUAGE_CODE_KEY) }
    }
}