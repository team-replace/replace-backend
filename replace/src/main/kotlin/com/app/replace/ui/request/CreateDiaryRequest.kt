package com.app.replace.ui.request

import com.app.replace.domain.Coordinate

data class CreateDiaryRequest(
    val title: String,
    val content: String,
    val shareScope: String,
    val coordinate: Coordinate,
    val images: List<String>
)
