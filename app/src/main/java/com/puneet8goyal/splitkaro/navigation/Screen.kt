package com.puneet8goyal.splitkaro.navigation

sealed class Screen(val route: String) {
    object Group : Screen("group")

    object Home : Screen("home/{groupId}") {
        fun createRoute(groupId: Long) = "home/$groupId"
    }

    object AddExpense : Screen("add_expense/{groupId}") {
        fun createRoute(groupId: Long) = "add_expense/$groupId"
    }
}