package com.ron.taskmanagement.viewmodels

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ron.taskmanagement.di.repository.RoomRepository
import com.ron.taskmanagement.models.Task
import com.ron.taskmanagement.ui.dialoges.FilterBottomSheet
import com.ron.taskmanagement.utils.RonConstants
import com.ron.taskmanagement.utils.launchWithResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val taskRepository: RoomRepository
) :
    ViewModel() {
    val taskStateFlow get() = taskRepository.taskStateFlow
    val totalPages get() = taskRepository.totalPages
    val filterApplied = MutableLiveData<Boolean>()

    private var isCompletedFragment: Boolean = false
    private val orderFilter = MutableLiveData<Boolean>()
    private val byDateFilter = MutableLiveData<String>()
    private val priorityType = MutableLiveData<String?>()
    private val toDate = MutableLiveData<String?>()
    private val fromDate = MutableLiveData<String?>()

    fun setCompletedList(value: Boolean) {
        isCompletedFragment = value
    }

    init {
        orderFilter.value = false
        byDateFilter.value = RonConstants.TaskDateFilterType.CreatedDate
        getTotalPagesCount()
    }


    fun getTaskListWithPaging(page: Int) {
        taskRepository.getTaskListWithFilter(
            isCompletedFragment,
            orderFilter.value ?: false,
            byDateFilter.value ?: "",
            page,
            fromDate.value,
            toDate.value,
            priorityType.value
        )
    }

    private fun getTotalPagesCount() {
        taskRepository.getTotalPages(
            isCompletedFragment,
            byDateFilter.value ?: RonConstants.TaskDateFilterType.ScheduledDate,
            fromDate.value,
            toDate.value,
            priorityType.value
        )
    }

    fun insertTask(task: Task) {
        taskRepository.insertTask(task)
    }

    fun deleteTask(task: Task) {
        taskRepository.deleteTask(task)
    }


    fun updateTask(task: Task) {
        taskRepository.updateTask(task)
    }


    fun searchTaskList(query: String) {
        taskRepository.searchTaskList(query)
    }


    fun saveExcel(selectedType: String, onCompleted: (String) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {

            val list = taskRepository.getAllListForExcel(
                selectedType,
                byDateFilter.value
                    ?: RonConstants.TaskDateFilterType.CreatedDate,
            )
            val workbook = HSSFWorkbook()
            val hssfSheet = workbook.createSheet("taskList")
            list.forEachIndexed { index, task ->
                val row = hssfSheet.createRow(index)
                row.createCell(0).setCellValue(task.taskId.toString())
                row.createCell(1).setCellValue(task.description)
                row.createCell(2).setCellValue(task.title)
                row.createCell(3).setCellValue(task.priority)
                row.createCell(4).setCellValue(task.date)
                row.createCell(5).setCellValue(task.createdAt)
                row.createCell(6).setCellValue(task.createdOn)
                row.createCell(7).setCellValue(task.completedOnTime)
                row.createCell(8).setCellValue(task.completed)
            }
            saveSheet(workbook, onCompleted)
        }
    }

    private fun saveSheet(workbook: HSSFWorkbook, onCompleted: (String) -> Unit) {
        val path =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path + "/${Date().time}.xls"
        val fileOutput = File(path)
        try {
            val outputStreamer = FileOutputStream(fileOutput)
            workbook.write(outputStreamer)
            outputStreamer.close()
            workbook.close()
            onCompleted("Download Completed \n Sheet saved At $path")
        } catch (e: Exception) {
            Log.e("saveSheet", ": Failed->   ${e.message}")
        }


    }

    fun openAddTaskPage(context: Context, addEditLauncher: ActivityResultLauncher<Intent>) {
        context.launchWithResult(
            RonConstants.FragmentsTypes.addTaskFragment,
            addEditLauncher
        )
    }

    fun openFilterSheet(context: Activity) {
        FilterBottomSheet(context)
            .openFilterSheet(
                sortByAccentingOrder = orderFilter.value ?: false,
                dateType = byDateFilter.value
                    ?: RonConstants.TaskDateFilterType.CreatedDate,
                priority = priorityType.value,
                fromDate = fromDate.value,
                toDate = toDate.value,
                applyFilters = { sortByAccentingOrder, dateType, priority, fromDate, toDate ->
                    orderFilter.value = sortByAccentingOrder
                    byDateFilter.value = dateType
                    priorityType.value = priority
                    this.fromDate.value = fromDate
                    this.toDate.value = toDate
                    getTotalPagesCount()
                    filterApplied.postValue(true)
                })
    }

}