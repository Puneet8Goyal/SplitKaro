package com.puneet8goyal.splitkaro.utils

import com.puneet8goyal.splitkaro.R

class ValidationHelper(private val appConfig: AppConfig) {

    fun validateExpenseDescription(description: String): String? {
        return if (description.trim().isEmpty()) {
            appConfig.getString(R.string.error_description_required)
        } else null
    }

    fun validateAmount(amount: String): String? {
        return when {
            amount.isEmpty() -> appConfig.getString(R.string.error_amount_required)
            amount.toDoubleOrNull()?.let { it <= 0 } != false ->
                appConfig.getString(R.string.error_amount_invalid)

            else -> null
        }
    }

    fun validatePaidBy(paidByMemberId: Long?): String? {
        return if (paidByMemberId == null) {
            appConfig.getString(R.string.error_paid_by_required)
        } else null
    }

    fun validateSplitMembers(memberIds: List<Long>): String? {
        return if (memberIds.isEmpty()) {
            appConfig.getString(R.string.error_split_members_required)
        } else null
    }

    fun validateMemberName(name: String): String? {
        return if (name.trim().isEmpty()) {
            appConfig.getString(R.string.error_member_name_required)
        } else null
    }

    fun validateCollectionName(name: String): String? {
        return if (name.trim().isEmpty()) {
            appConfig.getString(R.string.error_collection_name_required)
        } else null
    }
}
