package com.puneet8goyal.splitkaro.navigation

sealed class Screen(val route: String) {
    object ExpenseCollection : Screen("expense_collection")

    object Home : Screen("home/{collectionId}") {
        fun createRoute(collectionId: Long) = "home/$collectionId"
    }

    object AddExpense : Screen("add_expense/{collectionId}") {
        fun createRoute(collectionId: Long) = "add_expense/$collectionId"
    }

    object EditExpense : Screen("edit_expense/{expenseId}") {
        fun createRoute(expenseId: Long) = "edit_expense/$expenseId"
    }

    object Settlement : Screen("settlement/{collectionId}") {
        fun createRoute(collectionId: Long) = "settlement/$collectionId"
    }

    object ManageMembers : Screen("manage_members/{collectionId}") {
        fun createRoute(collectionId: Long) = "manage_members/$collectionId"
    }

    object EditCollection : Screen("edit_collection/{collectionId}") {
        fun createRoute(collectionId: Long) = "edit_collection/$collectionId"
    }
}