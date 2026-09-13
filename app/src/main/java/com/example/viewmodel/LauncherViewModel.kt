package com.example.viewmodel

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.AppInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LauncherViewModel(application: Application) : AndroidViewModel(application) {

    private val _currentTime = MutableStateFlow("06:28")
    val currentTime: StateFlow<String> = _currentTime.asStateFlow()

    private val _currentDate = MutableStateFlow("Thu, 13 Aug")
    val currentDate: StateFlow<String> = _currentDate.asStateFlow()

    private val _dayOfWeek = MutableStateFlow("FRIDAY")
    val dayOfWeek: StateFlow<String> = _dayOfWeek.asStateFlow()

    private val _installedApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val installedApps: StateFlow<List<AppInfo>> = _installedApps.asStateFlow()

    private val _is24HourFormat = MutableStateFlow(true)
    val is24HourFormat: StateFlow<Boolean> = _is24HourFormat.asStateFlow()

    init {
        // Start time/date updater coroutine
        viewModelScope.launch {
            while (isActive) {
                updateTimeAndDate()
                delay(1000)
            }
        }

        // Load installed apps
        loadInstalledApps()
    }

    private fun updateTimeAndDate() {
        val now = Date()
        val timePattern = if (_is24HourFormat.value) "HH:mm" else "hh:mm a"
        _currentTime.value = SimpleDateFormat(timePattern, Locale.getDefault()).format(now)
        _currentDate.value = SimpleDateFormat("EEE, d MMM", Locale.getDefault()).format(now)
        _dayOfWeek.value = SimpleDateFormat("EEEE", Locale.getDefault()).format(now).uppercase()
    }

    fun loadInstalledApps() {
        viewModelScope.launch {
            try {
                val pm = getApplication<Application>().packageManager
                val intent = Intent(Intent.ACTION_MAIN, null).apply {
                    addCategory(Intent.CATEGORY_LAUNCHER)
                }
                val activities = pm.queryIntentActivities(intent, 0)
                val allowedPrefixes = listOf("myToyota", "Toyota", "Lexus", "Zscaler", "Subaru")
                val appList = activities.mapNotNull { resolveInfo ->
                    val pkg = resolveInfo.activityInfo.packageName
                    if (pkg == getApplication<Application>().packageName) return@mapNotNull null
                    var label = resolveInfo.loadLabel(pm).toString()

                    val isAllowed = allowedPrefixes.any { prefix ->
                        label.startsWith(prefix, ignoreCase = true)
                    }
                    if (!isAllowed) return@mapNotNull null

                    // Append " - Stage" if package contains "stage"
                    if (pkg.contains("stage", ignoreCase = true)) {
                        label = "$label - Stage"
                    } else if (pkg.contains("oneapp", ignoreCase = true)) {
                        label = "$label - Prod"
                    }

                    val icon = resolveInfo.loadIcon(pm)
                    AppInfo(
                        label = label,
                        packageName = pkg,
                        icon = icon
                    )
                }.sortedBy { it.label.lowercase(Locale.getDefault()) }

                _installedApps.value = appList
            } catch (e: Exception) {
                Log.e("LauncherVM", "Error loading apps", e)
            }
        }
    }

    fun launchApp(packageName: String) {
        try {
            val pm = getApplication<Application>().packageManager
            val launchIntent = pm.getLaunchIntentForPackage(packageName)
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                getApplication<Application>().startActivity(launchIntent)
            }
        } catch (e: Exception) {
            Log.e("LauncherVM", "Failed to launch package $packageName", e)
        }
    }
}
