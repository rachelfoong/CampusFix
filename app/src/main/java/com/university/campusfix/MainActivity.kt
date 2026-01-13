package com.university.campusfix

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.university.campusfix.ui.CampusFixApp
import com.university.campusfix.ui.theme.CampusFixTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CampusFixTheme {
                // Main entry point for the Compose app
                CampusFixApp()
            }
        }
    }
}