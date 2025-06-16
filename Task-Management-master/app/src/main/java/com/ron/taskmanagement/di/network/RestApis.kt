package com.ron.taskmanagement.di.network


import com.ron.taskmanagement.models.TranslationApiResponse
import retrofit2.Response
import retrofit2.http.*


interface RestApis {
    @FormUrlEncoded
    @POST("api_endpoint")
    suspend fun getWordsMeaning(@FieldMap request: HashMap<String, String>): Response<TranslationApiResponse>

}
