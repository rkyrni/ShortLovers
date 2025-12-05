package com.app.shortlovers

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.app.shortlovers.ui.theme.ShortLoversTheme
import com.app.shortlovers.ui.view.MainView
import com.app.shortlovers.ui.view.auth.StartedView
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ShortLoversTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainView(modifier = Modifier.padding(innerPadding))
                }
//                Scaffold(
//                    modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    StartedView(modifier = Modifier.padding(innerPadding))
//                }
            }
        }
    }
}
