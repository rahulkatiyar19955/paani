package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.WaterTrackerScreen
import com.example.ui.WaterTrackerViewModel
import com.example.ui.theme.MyApplicationTheme
import com.example.services.AlarmScheduler
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    // Request notification permission on Android 13+ (API 33+)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
      }
    }

    // Schedule the first reminder alarm on initial app startup
    val prefs = getSharedPreferences("water_tracker_prefs", MODE_PRIVATE)
    if (!prefs.getBoolean("first_alarm_scheduled", false)) {
      lifecycleScope.launch {
        val repository = (application as App).repository
        val settings = repository.getSettingsSync()
        AlarmScheduler.scheduleNextAlarm(this@MainActivity, settings.intervalMinutes)
        prefs.edit().putBoolean("first_alarm_scheduled", true).apply()
      }
    }

    setContent {
      MyApplicationTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
          val viewModel: WaterTrackerViewModel = viewModel()
          WaterTrackerScreen(viewModel)
        }
      }
    }
  }
}
