package com.puneet8goyal.splitkaro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.puneet8goyal.splitkaro.navigation.Screen
import com.puneet8goyal.splitkaro.ui.screens.AddExpenseScreen
import com.puneet8goyal.splitkaro.ui.screens.HomeScreen
import com.puneet8goyal.splitkaro.ui.theme.SplitKaroTheme
import com.puneet8goyal.splitkaro.viewmodel.AddExpenseViewModel
import com.puneet8goyal.splitkaro.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SplitKaroTheme {
                App()
            }
        }
    }

    @Composable
    fun App() {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        val screens = listOf(
            Screen.Home,
            Screen.AddExpense
        )

        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                NavigationRail {
                    screens.forEach { screen ->
                        NavigationRailItem(
                            icon = { Text(screen.icon) },
                            label = { Text(screen.title) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
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

                NavHost(
                    navController = navController,
                    startDestination = Screen.Home.route,
                    modifier = Modifier.weight(1f)
                ) {
                    composable(Screen.Home.route) {
                        val homeViewModel: HomeViewModel = hiltViewModel()
                        HomeScreen(homeViewModel)
                    }

                    composable(Screen.AddExpense.route) {
                        val addExpenseViewModel: AddExpenseViewModel = hiltViewModel()
                        val homeViewModel: HomeViewModel = hiltViewModel()
                        AddExpenseScreen(
                            viewModel = addExpenseViewModel,
                            onSuccess = {
                                homeViewModel.refreshExpenses()
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Home.route) {
                                        inclusive = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}