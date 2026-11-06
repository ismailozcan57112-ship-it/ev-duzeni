package com.evduzeni.planner.util

import android.app.LocaleManager
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import com.evduzeni.planner.R
import java.util.Locale

data class LanguageOption(
    val code: String,
    val nativeName: String,
    val englishName: String,
    val flagEmoji: String
)

object AppLanguageHelper {

    val supportedLanguages: List<LanguageOption> = listOf(
        LanguageOption(code = "", nativeName = "Sistem Dili", englishName = "System Default", flagEmoji = "🌐"),
        LanguageOption(code = "en", nativeName = "English", englishName = "English", flagEmoji = "🇬🇧"),
        LanguageOption(code = "tr", nativeName = "Türkçe", englishName = "Turkish", flagEmoji = "🇹🇷"),
        LanguageOption(code = "de", nativeName = "Deutsch", englishName = "German", flagEmoji = "🇩🇪"),
        LanguageOption(code = "es", nativeName = "Español", englishName = "Spanish", flagEmoji = "🇪🇸"),
        LanguageOption(code = "fr", nativeName = "Français", englishName = "French", flagEmoji = "🇫🇷"),
        LanguageOption(code = "it", nativeName = "Italiano", englishName = "Italian", flagEmoji = "🇮🇹"),
        LanguageOption(code = "pt", nativeName = "Português", englishName = "Portuguese", flagEmoji = "🇵🇹"),
        LanguageOption(code = "ru", nativeName = "Русский", englishName = "Russian", flagEmoji = "🇷🇺"),
        LanguageOption(code = "ja", nativeName = "日本語", englishName = "Japanese", flagEmoji = "🇯🇵"),
        LanguageOption(code = "ko", nativeName = "한국어", englishName = "Korean", flagEmoji = "🇰🇷"),
        LanguageOption(code = "zh", nativeName = "简体中文", englishName = "Chinese (Simplified)", flagEmoji = "🇨🇳"),
        LanguageOption(code = "zh-TW", nativeName = "繁體中文", englishName = "Chinese (Traditional)", flagEmoji = "🇹🇼"),
        LanguageOption(code = "ar", nativeName = "العربية", englishName = "Arabic", flagEmoji = "🇸🇦"),
        LanguageOption(code = "hi", nativeName = "हिन्दी", englishName = "Hindi", flagEmoji = "🇮🇳"),
        LanguageOption(code = "id", nativeName = "Bahasa Indonesia", englishName = "Indonesian", flagEmoji = "🇮🇩"),
        LanguageOption(code = "ur", nativeName = "اردو", englishName = "Urdu", flagEmoji = "🇵🇰"),
        LanguageOption(code = "uk", nativeName = "Українська", englishName = "Ukrainian", flagEmoji = "🇺🇦"),
        LanguageOption(code = "nl", nativeName = "Nederlands", englishName = "Dutch", flagEmoji = "🇳🇱"),
        LanguageOption(code = "vi", nativeName = "Tiếng Việt", englishName = "Vietnamese", flagEmoji = "🇻🇳"),
        LanguageOption(code = "th", nativeName = "ไทย", englishName = "Thai", flagEmoji = "🇹🇭"),
        LanguageOption(code = "pl", nativeName = "Polski", englishName = "Polish", flagEmoji = "🇵🇱"),
        LanguageOption(code = "fil", nativeName = "Filipino", englishName = "Filipino / Tagalog", flagEmoji = "🇵🇭")
    )

    private val supportedCodes = setOf(
        "en", "tr", "de", "es", "fr", "it", "pt", "ru",
        "ja", "ko", "ar", "hi", "id", "in", "ur", "uk",
        "nl", "vi", "th", "pl", "fil", "tl"
    )

    fun getSystemLocale(): Locale {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val list = Resources.getSystem().configuration.locales
                if (!list.isEmpty) list.get(0) else Locale.getDefault()
            } else {
                @Suppress("DEPRECATION")
                Resources.getSystem().configuration.locale ?: Locale.getDefault()
            }
        } catch (_: Exception) {
            Locale.getDefault()
        }
    }

    fun parseLocale(code: String): Locale {
        return try {
            when (code) {
                "zh-TW" -> Locale.Builder().setLanguage("zh").setRegion("TW").build()
                "zh" -> Locale.SIMPLIFIED_CHINESE
                "fil" -> Locale.Builder().setLanguage("fil").build()
                else -> Locale.Builder().setLanguage(code).build()
            }
        } catch (_: Exception) {
            Locale.ENGLISH
        }
    }

    fun resolveLocale(selectedLanguage: String): Locale {
        return try {
            if (selectedLanguage.isNotBlank()) {
                return parseLocale(selectedLanguage)
            }

            val sysLocale = getSystemLocale()
            val lang = sysLocale.language.lowercase()
            val country = sysLocale.country.uppercase()
            val script = sysLocale.script.lowercase()

            if (lang == "zh") {
                return if (country == "TW" || country == "HK" || country == "MO" || script == "hant") {
                    Locale.Builder().setLanguage("zh").setRegion("TW").build()
                } else {
                    Locale.SIMPLIFIED_CHINESE
                }
            }

            if (lang == "id" || lang == "in") {
                return try { Locale.Builder().setLanguage("id").build() } catch (_: Exception) { Locale("id") }
            }

            if (lang == "fil" || lang == "tl") {
                return try { Locale.Builder().setLanguage("fil").build() } catch (_: Exception) { Locale("fil") }
            }

            if (supportedCodes.contains(lang)) {
                try { Locale.Builder().setLanguage(lang).build() } catch (_: Exception) { Locale(lang) }
            } else {
                // Unsupported device language -> default to English!
                Locale.ENGLISH
            }
        } catch (_: Exception) {
            Locale.ENGLISH
        }
    }

    fun applyLocale(context: Context, languageCode: String) {
        try {
            val locale = resolveLocale(languageCode)
            Locale.setDefault(locale)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                try {
                    val lm = context.getSystemService(LocaleManager::class.java)
                    if (languageCode.isBlank()) {
                        lm?.applicationLocales = LocaleList.getEmptyLocaleList()
                    } else {
                        lm?.applicationLocales = LocaleList.forLanguageTags(languageCode)
                    }
                } catch (_: Exception) {}
            }

            val res = context.resources
            val config = Configuration(res.configuration)
            config.setLocale(locale)
            config.setLayoutDirection(locale)
            @Suppress("DEPRECATION")
            res.updateConfiguration(config, res.displayMetrics)
        } catch (_: Exception) {}
    }

    fun getDisplayName(code: String, context: Context): String {
        if (code.isBlank()) {
            val resolved = resolveLocale("")
            val nativeResolved = supportedLanguages.find { it.code == resolved.language || (it.code == "zh-TW" && resolved.country == "TW") }?.nativeName ?: "English"
            return "${context.getString(R.string.language_system_default)} ($nativeResolved)"
        }
        val match = supportedLanguages.find { it.code == code }
        return if (match != null) {
            "${match.flagEmoji}  ${match.nativeName}"
        } else {
            code
        }
    }
}
