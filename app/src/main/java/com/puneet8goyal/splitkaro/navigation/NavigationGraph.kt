package com.puneet8goyal.splitkaro.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.puneet8goyal.splitkaro.ui.screens.AddExpenseScreen
import com.puneet8goyal.splitkaro.ui.screens.EditExpenseScreen
import com.puneet8goyal.splitkaro.ui.screens.ExpenseCollectionScreen
import com.puneet8goyal.splitkaro.ui.screens.HomeScreen
import com.puneet8goyal.splitkaro.ui.screens.ManageMembersScreen
import com.puneet8goyal.splitkaro.ui.screens.SettlementScreen
import com.puneet8goyal.splitkaro.viewmodel.AddExpenseViewModel
import com.puneet8goyal.splitkaro.viewmodel.EditExpenseViewModel
import com.puneet8goyal.splitkaro.viewmodel.ExpenseCollectionViewModel
import com.puneet8goyal.splitkaro.viewmodel.HomeViewModel
import com.puneet8goyal.splitkaro.viewmodel.ManageMembersViewModel
import com.puneet8goyal.splitkaro.viewmodel.SettlementViewModel

@Composable
fun AppNavigationGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.ExpenseCollection.route
    ) {
        composable(Screen.ExpenseCollection.route) { backStackEntry ->
            val collectionViewModel: ExpenseCollectionViewModel = hiltViewModel()
            val homeViewModel: HomeViewModel = hiltViewModel()

            val successMessage = backStackEntry.savedStateHandle.get<String>("success_message")
            val errorMessage = backStackEntry.savedStateHandle.get<String>("error_message")

            // Clear messages after reading
            backStackEntry.savedStateHandle.remove<String>("success_message")
            backStackEntry.savedStateHandle.remove<String>("error_message")

            ExpenseCollectionScreen(
                viewModel = collectionViewModel,
                homeViewModel = homeViewModel,
                initialSuccessMessage = successMessage,
                initialErrorMessage = errorMessage,
                onCollectionClick = { collectionId ->
                    navController.navigate(Screen.Home.createRoute(collectionId))
                }
            )
        }

        composable(Screen.Home.route) { backStackEntry ->
            val collectionId = backStackEntry.arguments?.getString("collectionId")?.toLongOrNull()
            if (collectionId == null) {
                navController.navigate(Screen.ExpenseCollection.route) {
                    popUpTo(Screen.ExpenseCollection.route) { inclusive = true }
                }
                return@composable
            }

            val homeViewModel: HomeViewModel = hiltViewModel()
            val collectionViewModel: ExpenseCollectionViewModel = hiltViewModel()

            val successMessage = backStackEntry.savedStateHandle.get<String>("success_message")
            val errorMessage = backStackEntry.savedStateHandle.get<String>("error_message")

            // Clear messages after reading
            backStackEntry.savedStateHandle.remove<String>("success_message")
            backStackEntry.savedStateHandle.remove<String>("error_message")

            HomeScreen(
                viewModel = homeViewModel,
                collectionViewModel = collectionViewModel,
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

        composable(Screen.AddExpense.route) { backStackEntry ->
            val collectionId = backStackEntry.arguments?.getString("collectionId")?.toLongOrNull()
            if (collectionId == null) {
                navController.navigate(Screen.ExpenseCollection.route) {
                    popUpTo(Screen.ExpenseCollection.route) { inclusive = true }
                }
                return@composable
            }

            val addExpenseViewModel: AddExpenseViewModel = hiltViewModel()
            val collectionViewModel: ExpenseCollectionViewModel = hiltViewModel()

            AddExpenseScreen(
                viewModel = addExpenseViewModel,
                collectionViewModel = collectionViewModel,
                collectionId = collectionId,
                onSuccess = {
                    navController.previousBackStackEntry?.savedStateHandle?.set("success_message", "✅ Expense added successfully!")
                    navController.popBackStack()
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.EditExpense.route) { backStackEntry ->
            val expenseId = backStackEntry.arguments?.getString("expenseId")?.toLongOrNull()
            if (expenseId == null) {
                navController.popBackStack()
                return@composable
            }

            val editExpenseViewModel: EditExpenseViewModel = hiltViewModel()
            val collectionViewModel: ExpenseCollectionViewModel = hiltViewModel()

            EditExpenseScreen(
                expenseId = expenseId,
                viewModel = editExpenseViewModel,
                collectionViewModel = collectionViewModel,
                onSuccess = {
                    navController.previousBackStackEntry?.savedStateHandle?.set("success_message", "✅ Expense updated successfully!")
                    navController.popBackStack()
                },
                onDelete = {
                    navController.previousBackStackEntry?.savedStateHandle?.set("success_message", "🗑️ Expense deleted successfully!")
                    navController.popBackStack()
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        // FIXED: Settlement screen - only pass success message when settlement actually happens
        composable(Screen.Settlement.route) { backStackEntry ->
            val collectionId = backStackEntry.arguments?.getString("collectionId")?.toLongOrNull()
            if (collectionId == null) {
                navController.popBackStack()
                return@composable
            }

            val settlementViewModel: SettlementViewModel = hiltViewModel()

            SettlementScreen(
                collectionId = collectionId,
                viewModel = settlementViewModel,
                onBackClick = { successMessage ->
                    // Only set success message if settlement actually happened
                    successMessage?.let {
                        navController.previousBackStackEntry?.savedStateHandle?.set("success_message", it)
                    }
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.ManageMembers.route) { backStackEntry ->
            val collectionId = backStackEntry.arguments?.getString("collectionId")?.toLongOrNull()
            if (collectionId == null) {
                navController.popBackStack()
                return@composable
            }

            val manageMembersViewModel: ManageMembersViewModel = hiltViewModel()

            ManageMembersScreen(
                collectionId = collectionId,
                viewModel = manageMembersViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.EditCollection.route) { backStackEntry ->
            val collectionId = backStackEntry.arguments?.getString("collectionId")?.toLongOrNull()
            if (collectionId == null) {
                navController.popBackStack()
                return@composable
            }

            navController.popBackStack()
        }
    }
}
