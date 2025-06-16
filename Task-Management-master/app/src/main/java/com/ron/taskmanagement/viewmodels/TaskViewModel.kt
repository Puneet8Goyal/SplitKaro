package com.ron.taskmanagement.viewmodels

import androidx.lifecycle.ViewModel
import com.ron.taskmanagement.models.Task
import com.ron.taskmanagement.di.repository.RoomRepository
import com.ron.taskmanagement.di.network.RestApis
import com.ron.taskmanagement.di.repository.RetrofitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskRepository: RoomRepository,
    private val apiRepositry: RetrofitRepository
) : ViewModel() {

    val statusLiveData get() = taskRepository.statusLiveData
    val wordsMeaning get() = apiRepositry.wordsMeaning


    fun insertTask(task: Task) {
        taskRepository.insertTask(task)
    }

    fun updateTask(task: Task) {
        taskRepository.updateTask(task)
    }

    fun getWordMeaning(word: String) {
        apiRepositry.getWordMeaning(word)
    }

}