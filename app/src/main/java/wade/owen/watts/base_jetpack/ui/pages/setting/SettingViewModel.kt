package wade.owen.watts.base_jetpack.ui.pages.setting

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import wade.owen.watts.base_jetpack.utils.LocaleManager
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(): ViewModel() {

    fun changeLanguage(context: Context, code: String) {
        LocaleManager.setNewLocale(context, code)
        if (context is Activity) {
            context.recreate()
        }
    }
}