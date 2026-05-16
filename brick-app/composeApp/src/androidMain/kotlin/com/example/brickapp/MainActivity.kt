
package com.example.brickapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Use 10.0.2.2 to reach host machine's localhost from Android emulator
            App(baseUrl = "http://10.0.2.2:8080")
        }
    }
}
