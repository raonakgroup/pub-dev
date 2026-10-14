package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.data.UiPackage
import com.example.ui.screens.*
import com.example.ui.theme.PubDevTheme
import com.example.ui.viewmodel.PubViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: PubViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PubDevTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PubApp(viewModel = viewModel)
                }
            }
        }
    }
}

sealed class Screen(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Home : Screen("home", "Explore", Icons.Default.Home)
    object Search : Screen("search", "Search", Icons.Default.Search)
    object Favorites : Screen("favorites", "Liked", Icons.Default.Favorite)
    object Account : Screen("account", "Account", Icons.Default.Person)
    object Detail : Screen("detail", "Detail", Icons.Default.Home)
}

@Composable
fun PubApp(viewModel: PubViewModel) {
    val navController = rememberNavController()
    val selectedPackage by viewModel.selectedPackage.collectAsState()

    val items = listOf(
        Screen.Home,
        Screen.Search,
        Screen.Favorites,
        Screen.Account
    )

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            if (currentRoute != Screen.Detail.route && currentRoute != Screen.Search.route) {
                NavigationBar {
                    items.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.title) },
                            label = { Text(screen.title) },
                            selected = currentRoute == screen.route,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onPackageClick = { pkg ->
                        viewModel.selectPackage(pkg)
                        navController.navigate(Screen.Detail.route)
                    },
                    onNavigateSearch = {
                        navController.navigate(Screen.Search.route)
                    }
                )
            }
            composable(Screen.Search.route) {
                SearchScreen(
                    viewModel = viewModel,
                    onPackageClick = { pkg ->
                        viewModel.selectPackage(pkg)
                        navController.navigate(Screen.Detail.route)
                    },
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
            composable(Screen.Favorites.route) {
                FavoritesScreen(
                    viewModel = viewModel,
                    onPackageClick = { pkg ->
                        viewModel.selectPackage(pkg)
                        navController.navigate(Screen.Detail.route)
                    }
                )
            }
            composable(Screen.Account.route) {
                AccountScreen(viewModel = viewModel)
            }
            composable(Screen.Detail.route) {
                selectedPackage?.let { pkg ->
                    DetailScreen(
                        pkg = pkg,
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
