
package com.example.brickapp

import androidx.compose.ui.window.ComposeUIViewController

fun MainViewController() = ComposeUIViewController {
    App(baseUrl = "http://localhost:8080")
}
