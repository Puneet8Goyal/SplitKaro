package com.ron.taskmanagement.di.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ron.taskmanagement.di.room.dao.TaskDao
import com.ron.taskmanagement.models.Task
import com.ron.taskmanagement.utils.Resource
import com.ron.taskmanagement.utils.RonConstants
import com.ron.taskmanagement.utils.StatusResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


class RoomRepository @Inject constructor(private val taskDao: TaskDao) {
    private val pageSize = 10
    private val _totalPages = MutableStateFlow<Resource<Int>>(Resource.Loading())
    val totalPages: StateFlow<Resource<Int>>
        get() = _totalPages


    private val _taskStateFlow = MutableStateFlow<Resource<Flow<List<Task>>>>(Resource.Loading())
    val taskStateFlow: StateFlow<Resource<Flow<List<Task>>>>
        get() = _taskStateFlow

    private val _statusLiveData = MutableLiveData<Resource<StatusResult>>()
    val statusLiveData: LiveData<Resource<StatusResult>>
        get() = _statusLiveData


    suspend fun getAllListForExcel(
        selectedType: String,
        dateTypeSort: String
    ): List<Task> {
        val type: Boolean? =
            when (selectedType) {
                RonConstants.DownloadExcelTypes.completedTasks -> true
                RonConstants.DownloadExcelTypes.pendingTasks -> false
                else -> null
            }
        return if (type == null) {
            taskDao.getTaskListWForExcel(false, dateTypeSort)
        } else {
            taskDao.getTaskListWForExcel(type, false, dateTypeSort)
        }
    }


    fun getTaskListWithFilter(
        isCompletedFragment: Boolean,
        isAsc: Boolean,
        dateTypeSort: String,
        page: Int,
        fromDate: String?,
        toDate: String?,
        priorityType: String?
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                _taskStateFlow.emit(Resource.Loading())
                delay(500)
                val result = if (!fromDate.isNullOrEmpty() && !toDate.isNullOrEmpty()) {
                    if (priorityType != null) {

                        taskDao.getTaskListWithFilters(
                            isCompletedFragment,
                            isAsc,
                            pageNumber = page,
                            pageSize = pageSize,
                            dateTypeSort = dateTypeSort,
                            fromDate = fromDate,
                            toDate = toDate,
                            priority = priorityType
                        )
                    } else {
                        taskDao.getTaskListWithFilters(
                            isCompletedFragment,
                            isAsc,
                            pageNumber = page,
                            pageSize = pageSize,
                            dateTypeSort = dateTypeSort,
                            fromDate = fromDate,
                            toDate = toDate
                        )
                    }
                } else if (!fromDate.isNullOrEmpty() && toDate.isNullOrEmpty()) {
                    if (priorityType != null) {
                        taskDao.getTaskListWithFilters(
                            isCompletedFragment,

                            isAsc,
                            pageNumber = page,
                            pageSize = pageSize,
                            dateTypeSort = dateTypeSort,
                            fromDate = fromDate,
                            toDate = fromDate,
                            priority = priorityType
                        )
                    } else {
                        taskDao.getTaskListWithFilters(
                            isCompletedFragment,
                            isAsc,
                            pageNumber = page,
                            pageSize = pageSize,
                            dateTypeSort = dateTypeSort,
                            fromDate = fromDate,
                            toDate = fromDate
                        )
                    }
                } else {
                    if (priorityType != null) {
                        taskDao.getTaskListWithFilters(
                            isCompletedFragment,
                            isAsc,
                            pageNumber = page,
                            pageSize = pageSize,
                            dateTypeSort = dateTypeSort,
                            priority = priorityType
                        )
                    } else {
                        taskDao.getTaskListWithFilters(
                            isCompletedFragment,
                            isAsc,
                            pageNumber = page,
                            pageSize = pageSize,
                            dateTypeSort = dateTypeSort
                        )
                    }
                }
                _taskStateFlow.emit(Resource.Success("Data Fetched Successfully", result))
            } catch (e: Exception) {
                _taskStateFlow.emit(Resource.Error(e.message.toString()))
            }
        }
    }


    fun insertTask(task: Task) {
        try {
            _statusLiveData.postValue(Resource.Loading())
            CoroutineScope(Dispatchers.IO).launch {
                val result = taskDao.insertTask(task)
                handleResult(result.toInt(), "Inserted Task Successfully", StatusResult.Added)
            }
        } catch (e: Exception) {
            _statusLiveData.postValue(Resource.Error(e.message.toString()))
        }
    }


    fun deleteTask(task: Task) {
        try {
            _statusLiveData.postValue(Resource.Loading())
            CoroutineScope(Dispatchers.IO).launch {
                val result = taskDao.deleteTask(task)
                handleResult(result, "Deleted Task Successfully", StatusResult.Deleted)
            }
        } catch (e: Exception) {
            _statusLiveData.postValue(Resource.Error(e.message.toString()))
        }
    }


    fun updateTask(task: Task) {
        try {
            _statusLiveData.postValue(Resource.Loading())
            CoroutineScope(Dispatchers.IO).launch {
                val result = taskDao.updateTask(task)
                handleResult(result, "Updated Task Successfully", StatusResult.Updated)

            }
        } catch (e: Exception) {
            _statusLiveData.postValue(Resource.Error(e.message.toString()))
        }
    }


    fun searchTaskList(query: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                _taskStateFlow.emit(Resource.Loading())
                val result = taskDao.searchTaskList("%${query}%")
                _taskStateFlow.emit(Resource.Success("loading", result))
            } catch (e: Exception) {
                _taskStateFlow.emit(Resource.Error(e.message.toString()))
            }
        }
    }


    private fun handleResult(result: Int, message: String, statusResult: StatusResult) {
        if (result != -1) {
            _statusLiveData.postValue(Resource.Success(message, statusResult))
        } else {
            _statusLiveData.postValue(Resource.Error("Something Went Wrong", statusResult))
        }
    }

    fun getTotalPages(
        isCompletedFragment: Boolean,
        dateTypeSort: String,
        fromDate: String?,
        toDate: String?,
        priorityType: String?
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = with(taskDao) {
                if (!fromDate.isNullOrEmpty() && !toDate.isNullOrEmpty()) {
                    if (!priorityType.isNullOrEmpty()) {
                        getTaskCount(
                            isCompletedFragment,
                            dateTypeSort,
                            fromDate,
                            toDate,
                            priorityType
                        )
                    } else {
                        getTaskCount(isCompletedFragment, dateTypeSort, fromDate, toDate)
                    }
                } else if (!fromDate.isNullOrEmpty() && toDate.isNullOrEmpty()) {
                    if (!priorityType.isNullOrEmpty()) {
                        getTaskCount(
                            isCompletedFragment,
                            dateTypeSort,
                            fromDate,
                            fromDate,
                            priorityType
                        )
                    } else {
                        getTaskCount(isCompletedFragment, dateTypeSort, fromDate, fromDate)
                    }
                } else {
                    if (!priorityType.isNullOrEmpty()) {
                        getTaskCount(isCompletedFragment, dateTypeSort, priorityType)
                    } else {
                        getTaskCount(isCompletedFragment, dateTypeSort)
                    }
                }
            }


            val count = result / pageSize + if (result % pageSize == 0) 0 else 1
            _totalPages.emit(Resource.Success("Total Pages", count))
        }
    }


}