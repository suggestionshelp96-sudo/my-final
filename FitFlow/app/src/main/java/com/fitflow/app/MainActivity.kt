package com.fitflow.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fitflow.app.data.FitDatabase
import com.fitflow.app.data.FitRepository
import com.fitflow.app.data.UserSettings
import com.fitflow.app.service.StepTrackingService
import com.fitflow.app.ui.FitFlowApp
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val permissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { startTracker() }
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); val repository = FitRepository(FitDatabase.create(applicationContext).dao()); setContent { FitFlowApp(FitViewModel(repository), ::requestTrackingPermissions) } }
    private fun requestTrackingPermissions() { val needed = buildList { if (Build.VERSION.SDK_INT >= 29 && ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) add(Manifest.permission.ACTIVITY_RECOGNITION); if (Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) add(Manifest.permission.POST_NOTIFICATIONS) }; if (needed.isEmpty()) startTracker() else permissions.launch(needed.toTypedArray()) }
    private fun startTracker() { ContextCompat.startForegroundService(this, Intent(this, StepTrackingService::class.java)) }
}

class FitViewModel(private val repository: FitRepository) : ViewModel() {
    val settings = repository.settings.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UserSettings())
    val habits = repository.habits.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    fun saveSettings(settings: UserSettings) = viewModelScope.launch { repository.save(settings) }
    fun addHabit(name: String) = viewModelScope.launch { repository.addHabit(name) }
}
