
package com.example.brickapp

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.brickapp.data.api.BrickApiClient
import com.example.brickapp.data.api.TokenManager
import com.example.brickapp.ui.auth.LoginScreen
import com.example.brickapp.ui.bricks.BrickDetailScreen
import com.example.brickapp.ui.bricks.BrickListScreen
import com.example.brickapp.ui.collection.MyCollectionScreen
import com.example.brickapp.ui.navigation.HomeTab
import com.example.brickapp.ui.navigation.Screen
import com.example.brickapp.ui.sets.SetDetailScreen
import com.example.brickapp.ui.sets.SetListScreen
import com.example.brickapp.ui.theme.BrickAppTheme

// Default to localhost; override per-platform if needed
private const val DEFAULT_BASE_URL = "http://localhost:8080"

@Composable
fun App(baseUrl: String = DEFAULT_BASE_URL) {
    val apiClient = remember { BrickApiClient(baseUrl) }
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Login) }

    BrickAppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            when (val screen = currentScreen) {
                is Screen.Login -> {
                    LoginScreen(
                        apiClient = apiClient,
                        onLoginSuccess = { currentScreen = Screen.Home }
                    )
                }
                is Screen.Home -> {
                    HomeScreen(
                        apiClient = apiClient,
                        onBrickClick = { id -> currentScreen = Screen.BrickDetail(id) },
                        onSetClick = { id -> currentScreen = Screen.SetDetail(id) },
                        onLogout = {
                            apiClient.logout()
                            currentScreen = Screen.Login
                        }
                    )
                }
                is Screen.BrickDetail -> {
                    BrickDetailScreen(
                        brickId = screen.brickId,
                        apiClient = apiClient,
                        onBack = { currentScreen = Screen.Home }
                    )
                }
                is Screen.SetDetail -> {
                    SetDetailScreen(
                        setId = screen.setId,
                        apiClient = apiClient,
                        onBack = { currentScreen = Screen.Home }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    apiClient: BrickApiClient,
    onBrickClick: (String) -> Unit,
    onSetClick: (String) -> Unit,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(HomeTab.BRICKS) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "BrickApp",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                actions = {
                    TokenManager.getDisplayName()?.let { name ->
                        Text(
                            text = name,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onLogout) {
                        Icon(
                            Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Logout",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                NavigationBarItem(
                    selected = selectedTab == HomeTab.BRICKS,
                    onClick = { selectedTab = HomeTab.BRICKS },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Bricks") }
                )
                NavigationBarItem(
                    selected = selectedTab == HomeTab.SETS,
                    onClick = { selectedTab = HomeTab.SETS },
                    icon = { Icon(Icons.Default.Star, contentDescription = null) },
                    label = { Text("Sets") }
                )
                NavigationBarItem(
                    selected = selectedTab == HomeTab.COLLECTION,
                    onClick = { selectedTab = HomeTab.COLLECTION },
                    icon = { Icon(Icons.Default.Star, contentDescription = null) },
                    label = { Text("Collection") }
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (selectedTab) {
                HomeTab.BRICKS -> BrickListScreen(apiClient, onBrickClick)
                HomeTab.SETS -> SetListScreen(apiClient, onSetClick)
                HomeTab.COLLECTION -> MyCollectionScreen(apiClient)
            }
        }
    }
}
