package com.puneet8goyal.splitkaro.navigation

sealed class Screen(
    val route: String,
    val title: String,
    val icon: String
) {
    object Home : Screen(
        route = "home",
        title = "Home",
        icon = "🏠"
    )

    object AddExpense : Screen(
        route = "add_expense",
        title = "Add",
        icon = "➕"
    )
}