package com.validador.pt.util

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LocaleManager {
    fun applyLocale(context: Context) {
        val config = context.resources.configuration
        val currentLocale = config.locales[0]
        
        if (currentLocale?.language == "pt") {
            setLocale(context, Locale("pt", "PT"))
        } else {
            setLocale(context, Locale.getDefault())
        }
    }
    
    private fun setLocale(context: Context, locale: Locale) {
        Locale.setDefault(locale)
        val config = context.resources.configuration
        config.setLocale(locale)
        context.resources.updateConfiguration(
            config,
            context.resources.displayMetrics
        )
    }

    fun isPortugueseSystemLanguage(context: Context): Boolean {
        val locale = context.resources.configuration.locales[0]
        return locale?.language == "pt"
    }
}
