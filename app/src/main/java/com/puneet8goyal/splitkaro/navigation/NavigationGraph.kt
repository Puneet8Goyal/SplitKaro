package com.puneet8goyal.splitkaro.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.puneet8goyal.splitkaro.ui.screens.AddExpenseScreen
import com.puneet8goyal.splitkaro.ui.screens.EditExpenseScreen
import com.puneet8goyal.splitkaro.ui.screens.ExpenseCollectionScreen
import com.puneet8goyal.splitkaro.ui.screens.HomeScreen
import com.puneet8goyal.splitkaro.ui.screens.ManageMembersScreen
import com.puneet8goyal.splitkaro.ui.screens.SettlementScreen

@Composable
fun AppNavigationGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screen.ExpenseCollection.route
    ) {
        // Expense Collection Screen
        composable(Screen.ExpenseCollection.route) { backStackEntry ->
            // Get and immediately clear messages from savedStateHandle
            val savedStateHandle = backStackEntry.savedStateHandle
            val successMessage = savedStateHandle.get<String>("successMessage")
            val errorMessage = savedStateHandle.get<String>("errorMessage")

            // Clear messages immediately after reading to prevent persistence
            LaunchedEffect(successMessage, errorMessage) {
                savedStateHandle.remove<String>("successMessage")
                savedStateHandle.remove<String>("errorMessage")
            }

            ExpenseCollectionScreen(
                initialSuccessMessage = successMessage,
                initialErrorMessage = errorMessage,
                onCollectionClick = { collectionId ->
                    navController.navigate(Screen.Home.createRoute(collectionId))
                }
            )
        }

        // Home (Expenses) Screen
        composable(
            route = Screen.Home.route,
            arguments = listOf(
                navArgument("collectionId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val collectionId = backStackEntry.arguments?.getLong("collectionId") ?: 0L

            // Get messages from savedStateHandle (from other screens)
            val savedStateHandle = backStackEntry.savedStateHandle
            val successMessage = savedStateHandle.get<String>("successMessage")
            val errorMessage = savedStateHandle.get<String>("errorMessage")

            // Clear messages immediately after reading
            LaunchedEffect(successMessage, errorMessage) {
                savedStateHandle.remove<String>("successMessage")
                savedStateHandle.remove<String>("errorMessage")
            }

            HomeScreen(
                collectionId = collectionId,
                initialSuccessMessage = successMessage,
                initialErrorMessage = errorMessage,
                onAddExpenseClick = {
                    navController.navigate(Screen.AddExpense.createRoute(collectionId))
                },
                onEditExpenseClick = { expenseId ->
                    navController.navigate(Screen.EditExpense.createRoute(expenseId))
                },
                onSettlementClick = {
                    navController.navigate(Screen.Settlement.createRoute(collectionId))
                },
                onManageMembersClick = {
                    navController.navigate(Screen.ManageMembers.createRoute(collectionId))
                }
            )
        }

        // Add Expense Screen
        composable(
            route = Screen.AddExpense.route,
            arguments = listOf(
                navArgument("collectionId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val collectionId = backStackEntry.arguments?.getLong("collectionId") ?: 0L

            AddExpenseScreen(
                collectionId = collectionId,
                onBackClick = {
                    navController.popBackStack()
                },
                onExpenseAdded = { successMessage ->
                    // Pass success message back to HomeScreen
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        "successMessage",
                        successMessage
                    )
                    navController.popBackStack()
                }
            )
        }

        // Edit Expense Screen
        composable(
            route = Screen.EditExpense.route,
            arguments = listOf(
                navArgument("expenseId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val expenseId = backStackEntry.arguments?.getLong("expenseId") ?: 0L

            EditExpenseScreen(
                expenseId = expenseId,
                onBackClick = {
                    navController.popBackStack()
                },
                onExpenseUpdated = { successMessage ->
                    // Pass success message back to HomeScreen
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        "successMessage",
                        successMessage
                    )
                    navController.popBackStack()
                },
                onExpenseDeleted = { successMessage ->
                    // Pass success message back to HomeScreen
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        "successMessage",
                        successMessage
                    )
                    navController.popBackStack()
                }
            )
        }

        // Settlement Screen
        composable(
            route = Screen.Settlement.route,
            arguments = listOf(
                navArgument("collectionId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val collectionId = backStackEntry.arguments?.getLong("collectionId") ?: 0L

            SettlementScreen(
                collectionId = collectionId,
                onBackClick = { successMessage ->
                    if (successMessage != null) {
                        // Pass success message back to HomeScreen
                        navController.previousBackStackEntry?.savedStateHandle?.set(
                            "successMessage",
                            successMessage
                        )
                    }
                    navController.popBackStack()
                }
            )
        }

        // Manage Members Screen
        composable(
            route = Screen.ManageMembers.route,
            arguments = listOf(
                navArgument("collectionId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val collectionId = backStackEntry.arguments?.getLong("collectionId") ?: 0L

            ManageMembersScreen(
                collectionId = collectionId,
                onBackClick = { successMessage ->
                    if (successMessage != null) {
                        // Pass success message back to HomeScreen
                        navController.previousBackStackEntry?.savedStateHandle?.set(
                            "successMessage",
                            successMessage
                        )
                    }
                    navController.popBackStack()
                }
            )
        }
    }
}
