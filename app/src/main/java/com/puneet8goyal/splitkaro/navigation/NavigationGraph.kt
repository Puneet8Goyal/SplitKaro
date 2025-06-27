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
        // Collection List Screen
        composable(Screen.ExpenseCollection.route) {
            val collectionViewModel: ExpenseCollectionViewModel = hiltViewModel()
            ExpenseCollectionScreen(
                viewModel = collectionViewModel,
                onCollectionClick = { collectionId ->
                    navController.navigate(Screen.Home.createRoute(collectionId))
                }
            )
        }

        // Home/Expenses Screen
        composable(Screen.Home.route) { backStackEntry ->
            val collectionId = backStackEntry.arguments?.getString("collectionId")?.toLongOrNull()

            if (collectionId == null) {
                navController.navigate(Screen.ExpenseCollection.route) {
                    popUpTo(Screen.ExpenseCollection.route) { inclusive = true }
                }
                return@composable
            }

            val homeViewModel: HomeViewModel = hiltViewModel()
            HomeScreen(
                viewModel = homeViewModel,
                collectionViewModel = hiltViewModel(),
                collectionId = collectionId,
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
        composable(Screen.AddExpense.route) { backStackEntry ->
            val collectionId = backStackEntry.arguments?.getString("collectionId")?.toLongOrNull()

            if (collectionId == null) {
                navController.navigate(Screen.ExpenseCollection.route) {
                    popUpTo(Screen.ExpenseCollection.route) { inclusive = true }
                }
                return@composable
            }

            val addExpenseViewModel: AddExpenseViewModel = hiltViewModel()
            AddExpenseScreen(
                viewModel = addExpenseViewModel,
                collectionViewModel = hiltViewModel(),
                collectionId = collectionId,
                onSuccess = {
                    navController.navigate(Screen.Home.createRoute(collectionId)) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        // Edit Expense Screen
        composable(Screen.EditExpense.route) { backStackEntry ->
            val expenseId = backStackEntry.arguments?.getString("expenseId")?.toLongOrNull()

            if (expenseId == null) {
                navController.popBackStack()
                return@composable
            }

            val editExpenseViewModel: EditExpenseViewModel = hiltViewModel()
            EditExpenseScreen(
                expenseId = expenseId,
                viewModel = editExpenseViewModel,
                collectionViewModel = hiltViewModel(),
                onSuccess = {
                    navController.popBackStack()
                },
                onDelete = {
                    navController.popBackStack()
                }
            )
        }

        // Settlement Screen
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
                onBackClick = { navController.popBackStack() }
            )
        }

        // Manage Members Screen
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

        // Edit Collection Screen (placeholder)
        composable(Screen.EditCollection.route) { backStackEntry ->
            val collectionId = backStackEntry.arguments?.getString("collectionId")?.toLongOrNull()

            if (collectionId == null) {
                navController.popBackStack()
                return@composable
            }

            // For now, just navigate back
            navController.popBackStack()
        }
    }
}