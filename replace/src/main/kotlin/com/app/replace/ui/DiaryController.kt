package com.app.replace.ui

import com.app.replace.application.DiaryService
import com.app.replace.application.SingleDiaryRecord
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.net.URI

@RestController
@RequestMapping("/diary")
class DiaryController(val diaryService: DiaryService) {

    @PostMapping
    fun createDiary(@RequestBody createDiaryRequest: CreateDiaryRequest): ResponseEntity<Long> {
        val diaryId = diaryService.createDiary(
            createDiaryRequest.title,
            createDiaryRequest.content,
            createDiaryRequest.shareScope,
            createDiaryRequest.images
        )

        return ResponseEntity.created(URI.create("/url/${diaryId}")).build()
    }

    @PostMapping("/images")
    fun uploadImages(@ModelAttribute imageUploadingRequest: ImageUploadingRequest): ResponseEntity<ImageUploadingResponse> {
        val imageUrls = diaryService.uploadImages(imageUploadingRequest.images)
        return ResponseEntity
            .status(HttpStatusCode.valueOf(HttpStatus.CREATED.value()))
            .body(ImageUploadingResponse(imageUrls))
    }

    @GetMapping("/{diaryId}")
    fun loadSingleDiary(@PathVariable diaryId: Long): ResponseEntity<SingleDiaryRecord> {
        val diary = diaryService.loadSingleDiary(diaryId)
        return ResponseEntity.ok(diary)
    }

    @PutMapping("/{diaryId}")
    @ResponseStatus(HttpStatus.OK)
    fun updateDiary(@PathVariable diaryId: Long, @RequestBody createDiaryRequest: CreateDiaryRequest) {
        diaryService.updateDiary(
            diaryId,
            createDiaryRequest.title,
            createDiaryRequest.content,
            createDiaryRequest.shareScope,
            createDiaryRequest.images
        )
    }

    @DeleteMapping("/{diaryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteDiary(@PathVariable diaryId: Long) {
        diaryService.deleteDiary(diaryId)
    }
}

data class CreateDiaryRequest(val title: String, val content: String, val shareScope: String, val images: List<String>)

data class ImageUploadingRequest(val images: List<MultipartFile>)
data class ImageUploadingResponse(val imageUrls: List<String>)
