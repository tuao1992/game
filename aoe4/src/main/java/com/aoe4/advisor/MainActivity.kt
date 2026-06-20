package com.aoe4.advisor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.aoe4.advisor.ui.Aoe4App
import com.aoe4.advisor.ui.theme.Aoe4AdvisorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Aoe4AdvisorTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Aoe4App()
                }
            }
        }
    }
}
