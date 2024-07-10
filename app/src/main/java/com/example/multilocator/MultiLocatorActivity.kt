package com.example.multilocator

import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.platform.LocalContext
import com.example.multilocator.ui.theme.MultiLocatorTheme
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MultiLocatorActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED

       /* window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )*/

        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT))

        setContent {
            MultiLocatorApp()
        }
    }
}
