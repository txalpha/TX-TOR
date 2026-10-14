package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.model.SearchEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BrowserPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("txtor_prefs", Context.MODE_PRIVATE)

    private val _searchEngine = MutableStateFlow(getSavedSearchEngine())
    val searchEngine: StateFlow<SearchEngine> = _searchEngine.asStateFlow()

    private val _desktopModeDefault = MutableStateFlow(prefs.getBoolean(KEY_DESKTOP_DEFAULT, false))
    val desktopModeDefault: StateFlow<Boolean> = _desktopModeDefault.asStateFlow()

    private val _trackerBlockerEnabled = MutableStateFlow(prefs.getBoolean(KEY_TRACKER_BLOCKER, true))
    val trackerBlockerEnabled: StateFlow<Boolean> = _trackerBlockerEnabled.asStateFlow()

    private val _clearOnExit = MutableStateFlow(prefs.getBoolean(KEY_CLEAR_ON_EXIT, false))
    val clearOnExit: StateFlow<Boolean> = _clearOnExit.asStateFlow()

    private val _amoledTheme = MutableStateFlow(prefs.getBoolean(KEY_AMOLED_THEME, true))
    val amoledTheme: StateFlow<Boolean> = _amoledTheme.asStateFlow()

    private val _homepageUrl = MutableStateFlow(prefs.getString(KEY_HOMEPAGE_URL, "about:home") ?: "about:home")
    val homepageUrl: StateFlow<String> = _homepageUrl.asStateFlow()

    fun setSearchEngine(engine: SearchEngine) {
        prefs.edit().putString(KEY_SEARCH_ENGINE, engine.name).apply()
        _searchEngine.value = engine
    }

    fun setDesktopModeDefault(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DESKTOP_DEFAULT, enabled).apply()
        _desktopModeDefault.value = enabled
    }

    fun setTrackerBlockerEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_TRACKER_BLOCKER, enabled).apply()
        _trackerBlockerEnabled.value = enabled
    }

    fun setClearOnExit(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_CLEAR_ON_EXIT, enabled).apply()
        _clearOnExit.value = enabled
    }

    fun setAmoledTheme(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_AMOLED_THEME, enabled).apply()
        _amoledTheme.value = enabled
    }

    fun setHomepageUrl(url: String) {
        prefs.edit().putString(KEY_HOMEPAGE_URL, url).apply()
        _homepageUrl.value = url
    }

    private fun getSavedSearchEngine(): SearchEngine {
        val saved = prefs.getString(KEY_SEARCH_ENGINE, SearchEngine.GOOGLE.name)
        return try {
            SearchEngine.valueOf(saved ?: SearchEngine.GOOGLE.name)
        } catch (_: Exception) {
            SearchEngine.GOOGLE
        }
    }

    companion object {
        private const val KEY_SEARCH_ENGINE = "pref_search_engine"
        private const val KEY_DESKTOP_DEFAULT = "pref_desktop_default"
        private const val KEY_TRACKER_BLOCKER = "pref_tracker_blocker"
        private const val KEY_CLEAR_ON_EXIT = "pref_clear_on_exit"
        private const val KEY_AMOLED_THEME = "pref_amoled_theme"
        private const val KEY_HOMEPAGE_URL = "pref_homepage_url"
    }
}
