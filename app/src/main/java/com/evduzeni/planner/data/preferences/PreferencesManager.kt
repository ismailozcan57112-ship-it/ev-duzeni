package com.evduzeni.planner.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.evduzeni.planner.AppConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "evduzeni_prefs")

data class AppPreferences(
    val onboardingDone: Boolean,
    val notificationsEnabled: Boolean,
    val reminderHour: Int,
    val reminderMinute: Int,
    val promoDialogShown: Boolean,
    val hasProLifetime: Boolean,
    val appLanguage: String = ""
)

class PreferencesManager(private val context: Context) {

    private object Keys {
        val ONBOARDING_DONE = booleanPreferencesKey("onboarding_done")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val REMINDER_HOUR = intPreferencesKey("reminder_hour")
        val REMINDER_MINUTE = intPreferencesKey("reminder_minute")
        val PROMO_DIALOG_SHOWN = booleanPreferencesKey("promo_dialog_shown")
        val HAS_PRO_LIFETIME = booleanPreferencesKey("has_pro_lifetime")
        val APP_LANGUAGE = stringPreferencesKey("app_language")
    }

    val preferencesFlow: Flow<AppPreferences> = context.dataStore.data.map { prefs ->
        AppPreferences(
            onboardingDone = prefs[Keys.ONBOARDING_DONE] ?: false,
            notificationsEnabled = prefs[Keys.NOTIFICATIONS_ENABLED] ?: false,
            reminderHour = prefs[Keys.REMINDER_HOUR] ?: AppConfig.DEFAULT_REMINDER_HOUR,
            reminderMinute = prefs[Keys.REMINDER_MINUTE] ?: AppConfig.DEFAULT_REMINDER_MINUTE,
            promoDialogShown = prefs[Keys.PROMO_DIALOG_SHOWN] ?: false,
            hasProLifetime = if (AppConfig.IS_PROMOTIONAL_FREE_MODE) true else (prefs[Keys.HAS_PRO_LIFETIME] ?: false),
            appLanguage = prefs[Keys.APP_LANGUAGE] ?: ""
        )
    }

    suspend fun setOnboardingDone(done: Boolean) {
        context.dataStore.edit { it[Keys.ONBOARDING_DONE] = done }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.NOTIFICATIONS_ENABLED] = enabled }
    }

    suspend fun setReminderTime(hour: Int, minute: Int) {
        context.dataStore.edit {
            it[Keys.REMINDER_HOUR] = hour
            it[Keys.REMINDER_MINUTE] = minute
        }
    }

    suspend fun setPromoDialogShown(shown: Boolean) {
        context.dataStore.edit { it[Keys.PROMO_DIALOG_SHOWN] = shown }
    }

    suspend fun setHasProLifetime(pro: Boolean) {
        context.dataStore.edit { it[Keys.HAS_PRO_LIFETIME] = pro }
    }

    suspend fun setAppLanguage(languageCode: String) {
        context.dataStore.edit { it[Keys.APP_LANGUAGE] = languageCode }
        try {
            context.getSharedPreferences("evduzeni_quick_prefs", Context.MODE_PRIVATE)
                .edit().putString("app_language", languageCode).apply()
        } catch (_: Exception) {}
    }
}
