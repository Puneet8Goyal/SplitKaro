package com.puneet8goyal.splitkaro.utils

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("splitkaro_user", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_CURRENT_USER_NAME = "current_user_name"
        private const val KEY_CURRENT_USER_MEMBER_ID = "current_user_member_id"
    }

    fun setCurrentUserName(name: String) {
        prefs.edit().putString(KEY_CURRENT_USER_NAME, name).apply()
    }

    fun getCurrentUserName(): String? {
        return prefs.getString(KEY_CURRENT_USER_NAME, null)
    }

    fun setCurrentUserMemberId(memberId: Long) {
        prefs.edit().putLong(KEY_CURRENT_USER_MEMBER_ID, memberId).apply()
    }

    fun getCurrentUserMemberId(): Long {
        return prefs.getLong(KEY_CURRENT_USER_MEMBER_ID, -1L)
    }

    fun isFirstTimeUser(): Boolean {
        return getCurrentUserName() == null
    }
}
