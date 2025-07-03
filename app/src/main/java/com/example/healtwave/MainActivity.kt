package com.example.healtwave

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.healtwave.ui.navigation.NavGraph
import com.example.healtwave.ui.theme.HealtWaveTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HealtWaveTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}