package com.example.levelupgamer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.levelupgamer.navigation.AppNavigation
import com.example.levelupgamer.ui.theme.LevelUpGamerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            LevelUpGamerTheme {
                AppNavigation()
            }
        }
    }
}