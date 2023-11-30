package com.app.replace.ui

import com.app.replace.application.DiaryService
import com.app.replace.application.response.DiaryPreviews
import com.app.replace.application.response.SingleDiaryRecord
import com.app.replace.ui.argumentresolver.Authenticated
import com.app.replace.ui.request.CreateDiaryRequest
import com.app.replace.ui.request.ImageUploadingRequest
import com.app.replace.ui.response.ImageUploadingResponse
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI
import java.time.LocalDate

@RestController
class DiaryController(val diaryService: DiaryService) {

    @PostMapping("/diary")
    fun createDiary(
        @Authenticated userId: Long,
        @RequestBody createDiaryRequest: CreateDiaryRequest
    ): ResponseEntity<Long> {
        val diaryId = diaryService.createDiary(
            userId,
            createDiaryRequest.title,
            createDiaryRequest.content,
            createDiaryRequest.shareScope,
            createDiaryRequest.images
        )

        return ResponseEntity.created(URI.create("/url/${diaryId}")).build()
    }

    @PostMapping("/diary/images")
    fun uploadImages(
        @ModelAttribute imageUploadingRequest: ImageUploadingRequest
    ): ResponseEntity<ImageUploadingResponse> {
        val imageUrls = diaryService.uploadImages(imageUploadingRequest.images)
        return ResponseEntity
            .status(HttpStatusCode.valueOf(HttpStatus.CREATED.value()))
            .body(ImageUploadingResponse(imageUrls))
    }

    @GetMapping("/diary/{diaryId}")
    fun loadSingleDiary(
        @PathVariable diaryId: Long
    ): ResponseEntity<SingleDiaryRecord> {
        val diary = diaryService.loadSingleDiary(diaryId)
        return ResponseEntity.ok(diary)
    }

    @PutMapping("/diary/{diaryId}")
    @ResponseStatus(HttpStatus.OK)
    fun updateDiary(
        @Authenticated userId: Long,
        @PathVariable diaryId: Long,
        @RequestBody createDiaryRequest: CreateDiaryRequest
    ) {
        diaryService.updateDiary(
            userId,
            diaryId,
            createDiaryRequest.title,
            createDiaryRequest.content,
            createDiaryRequest.shareScope,
            createDiaryRequest.images
        )
    }

    @DeleteMapping("/diary/{diaryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteDiary(
        @Authenticated userId: Long,
        @PathVariable diaryId: Long
    ) {
        diaryService.deleteDiary(diaryId)
    }

    @GetMapping("/diarys")
    fun loadDiaries(
        @Authenticated userId: Long,
        @RequestParam(name = "year") year: Int,
        @RequestParam(name = "month") month: Int,
        @RequestParam(name = "day") day: Int
    ): ResponseEntity<DiaryPreviews> {
        val localDate = LocalDate.of(year, month, day)
        return ResponseEntity.ok(diaryService.loadDiaries(userId, localDate))
    }
}

