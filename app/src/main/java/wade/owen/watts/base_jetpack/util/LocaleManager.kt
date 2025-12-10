package wade.owen.watts.base_jetpack.util

import android.annotation.TargetApi
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import wade.owen.watts.base_jetpack.data.local.SharedPrefs
import java.util.Locale

object LocaleManager {

    fun setLocale(context: Context): Context {
        return updateResources(context, getLanguage(context))
    }

    fun setNewLocale(context: Context, language: String): Context {
        persistLanguage(context, language)
        return updateResources(context, language)
    }

    private fun getLanguage(context: Context): String {
        return SharedPrefs.getLanguageCode() ?: "en"
    }

    private fun persistLanguage(context: Context, language: String) {
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
