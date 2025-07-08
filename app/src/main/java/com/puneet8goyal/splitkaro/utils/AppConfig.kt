package com.puneet8goyal.splitkaro.utils

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppConfig @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // String resource helper
    fun getString(resId: Int): String {
        return context.getString(resId)
    }
}
