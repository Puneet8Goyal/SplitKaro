package com.ron.taskmanagement.di.repository

import androidx.lifecycle.MutableLiveData
import com.ron.taskmanagement.di.network.RestApis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class RetrofitRepository @Inject constructor(private val restApis: RestApis) {
    val wordsMeaning = MutableLiveData<String>()


    private var wordJob: Job? = null
    fun getWordMeaning(word: String) {
        wordsMeaning.value = "-----"
        wordJob?.cancel()
        wordJob = CoroutineScope(Dispatchers.IO).launch {
            val response = restApis.getWordsMeaning(HashMap<String, String>().also {
                it["word"] = word
            })
            if (response.isSuccessful) {
                if (response.body() != null) {
                    response.body()?.meaning?.let {
                        wordsMeaning.value = it
                    }
                }
            }
        }

    }
}