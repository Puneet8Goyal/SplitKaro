package com.ron.taskmanagement.models

import org.json.JSONObject

data class TranslationApiResponse(
    val status: String?,
    val message: String?,
    val metaInfo: JSONObject?,
    val meaning: String?
)