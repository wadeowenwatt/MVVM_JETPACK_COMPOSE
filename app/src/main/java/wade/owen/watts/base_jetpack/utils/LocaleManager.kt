package wade.owen.watts.base_jetpack.utils

import android.content.Context
import android.content.res.Configuration
import wade.owen.watts.base_jetpack.data.local.SharedPrefs
import java.util.Locale

object LocaleManager {

    fun setLocale(context: Context): Context {
        return updateResources(context, getLanguage())
    }

    fun setNewLocale(context: Context, language: String): Context {
        persistLanguage(language)
        return updateResources(context, language)
    }

    private fun getLanguage(): String {
        return SharedPrefs.getLanguageCode() ?: "en"
    }

    private fun persistLanguage(language: String) {
        SharedPrefs.setLanguageCode(language)
    }

    private fun updateResources(context: Context, language: String): Context {
        val locale = Locale.Builder().setLanguage(language).build()
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        return context.createConfigurationContext(config)
    }
}