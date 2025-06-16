package com.ron.taskmanagement.utils

interface RonConstants {
    object IntentStrings {
        const val type = "intentType"
        const val payload = "payload"
        const val data = "data"
    }

    object FragmentsTypes {
        const val addTaskFragment = "Add Task"
        const val pendingTasksList = "Pending"
        const val completedTaskList = "Completed"
        const val taskTabFragment = "TaskTabFragment"
    }

    object TaskPriorities {
        const val Low = "Low"
        const val Medium = "Medium"
        const val High = "High"
    }

    object TaskDateFilterType {
        const val CreatedDate = "CreatedDate"
        const val ScheduledDate = "ScheduledDate"
    }
    object DownloadExcelTypes {
        const val allTasks = "AllTasks"
        const val completedTasks = "CompletedTasks"
        const val pendingTasks = "pendingTasks"
    }

}