package com.app.replace.ui.request

data class UpdateDiaryRequest(
    val title: String,
    val content: String,
    val shareScope: String,
    val images: List<String>
)
