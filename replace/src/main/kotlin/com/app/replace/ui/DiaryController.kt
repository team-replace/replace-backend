package com.app.replace.ui

import com.app.replace.application.DiaryService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
class DiaryController(val diaryService: DiaryService) {

    @PostMapping("/diary")
    fun createDiary(@RequestBody createDiaryRequest: CreateDiaryRequest) : ResponseEntity<Long> {
        val diaryId = diaryService.createDiary(
            createDiaryRequest.title,
            createDiaryRequest.content,
            createDiaryRequest.shareScope,
            createDiaryRequest.images
        )

        return ResponseEntity.created(URI.create("/url/${diaryId}")).build()
    }
}

data class CreateDiaryRequest(val title: String, val content: String, val shareScope: String, val images: List<String>)
