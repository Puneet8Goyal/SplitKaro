package com.puneet8goyal.splitkaro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.puneet8goyal.splitkaro.navigation.Screen
import com.puneet8goyal.splitkaro.ui.screens.AddExpenseScreen
import com.puneet8goyal.splitkaro.ui.screens.GroupScreen
import com.puneet8goyal.splitkaro.ui.screens.HomeScreen
import com.puneet8goyal.splitkaro.ui.theme.SplitKaroTheme
import com.puneet8goyal.splitkaro.viewmodel.AddExpenseViewModel
import com.puneet8goyal.splitkaro.viewmodel.GroupViewModel
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

        NavHost(
            navController = navController,
            startDestination = Screen.Group.route,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(Screen.Group.route) {
                val groupViewModel: GroupViewModel = hiltViewModel()
                GroupScreen(
                    viewModel = groupViewModel,
                    onGroupClick = { groupId ->
                        navController.navigate(Screen.Home.createRoute(groupId))
                    }
                )
            }
            composable(Screen.Home.route) { backStackEntry ->
                val groupId = backStackEntry.arguments?.getString("groupId")?.toLong() ?: 0L
                val homeViewModel: HomeViewModel = hiltViewModel()
                HomeScreen(viewModel = homeViewModel, groupId = groupId, onAddExpenseClick = {
                    navController.navigate(Screen.AddExpense.createRoute(groupId))
                })
            }

            composable(Screen.AddExpense.route) { backStackEntry ->
                val groupId = backStackEntry.arguments?.getString("groupId")?.toLong() ?: 0L
                val addExpenseViewModel: AddExpenseViewModel = hiltViewModel()
                AddExpenseScreen(
                    viewModel = addExpenseViewModel,
                    groupId = groupId,
                    onSuccess = {
                        navController.navigate(Screen.Home.createRoute(groupId)) {
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