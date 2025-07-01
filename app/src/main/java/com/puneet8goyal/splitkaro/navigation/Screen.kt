package com.puneet8goyal.splitkaro.navigation

sealed class Screen(val route: String) {
    object ExpenseCollection : Screen("expense_collection")

    object Home : Screen("home/{collectionId}") {
        fun createRoute(collectionId: Long): String {
            return "home/$collectionId"
        }
    }

    object AddExpense : Screen("add_expense/{collectionId}") {
        fun createRoute(collectionId: Long): String {
            return "add_expense/$collectionId"
        }
    }

    object EditExpense : Screen("edit_expense/{expenseId}") {
        fun createRoute(expenseId: Long): String {
            return "edit_expense/$expenseId"
        }
    }

    object Settlement : Screen("settlement/{collectionId}") {
        fun createRoute(collectionId: Long): String {
            return "settlement/$collectionId"
        }
    }

    object ManageMembers : Screen("manage_members/{collectionId}") {
        fun createRoute(collectionId: Long): String {
            return "manage_members/$collectionId"
        }
    }
}