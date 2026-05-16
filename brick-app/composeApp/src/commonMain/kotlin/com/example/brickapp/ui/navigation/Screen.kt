
package com.example.brickapp.ui.navigation

/**
 * Sealed class representing all navigation destinations in the app.
 */
sealed class Screen {
    data object Login : Screen()
    data object Home : Screen()
    data class BrickDetail(val brickId: String) : Screen()
    data class SetDetail(val setId: String) : Screen()
}

/**
 * Tabs within the Home screen.
 */
enum class HomeTab(val title: String) {
    BRICKS("Bricks"),
    SETS("Sets"),
    COLLECTION("My Collection")
}
