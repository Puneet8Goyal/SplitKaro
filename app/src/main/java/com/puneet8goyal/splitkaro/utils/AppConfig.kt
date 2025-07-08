package com.puneet8goyal.splitkaro.utils

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppConfig @Inject constructor(
    @ApplicationContext private val context: Context
) {

    // Animation durations
    val shortAnimation = 300L
    val mediumAnimation = 400L
    val longAnimation = 500L

    // Message display times
    val successMessageDelay = 3000L
    val errorMessageDelay = 4000L

    // Database settings
    val databaseName = "splitkaro_database"

    // String resource helper
    fun getString(resId: Int): String {
        return context.getString(resId)
    }

    fun getString(resId: Int, vararg formatArgs: Any): String {
        return context.getString(resId, *formatArgs)
    }
}
