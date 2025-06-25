package com.puneet8goyal.splitkaro.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val description: String,
    val amount: Double,
    val paidBy: String,
    val splitAmong: Int,
    val perPersonAmount: Double

)
