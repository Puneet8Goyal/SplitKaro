package com.puneet8goyal.splitkaro.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expense_collections")
data class ExpenseCollection(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val createdAt: Long = System.currentTimeMillis()
)