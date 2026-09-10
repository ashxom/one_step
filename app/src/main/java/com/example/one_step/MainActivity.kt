package com.example.one_step

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.one_step.ui.OneStepApp
import com.example.one_step.ui.theme.One_stepTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            One_stepTheme {
                OneStepApp()
            }
        }
    }
}
