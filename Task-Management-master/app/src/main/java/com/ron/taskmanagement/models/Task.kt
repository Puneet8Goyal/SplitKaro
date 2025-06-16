package com.ron.taskmanagement.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity()
data class Task(
    @ColumnInfo(name = "taskTitle")
    val title: String,
    val description: String,
    val date: String,
    val priority: String,
    val duration: String,
    val createdOn: String,
    val createdAt: String,
    var completedOnTime: String,
    var completed: Boolean = false,
) :Serializable{
    @PrimaryKey(autoGenerate = true)
    var taskId: Int = 0
}
