package com.app.replace.application

import com.app.replace.domain.*
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.time.Instant
import java.time.format.DateTimeFormatter

@Service
class DiaryService(
    private val diaryRepository : DiaryRepository,
    private val imageService: ImageService
) {
    fun createDiary(title: String, content: String, shareScope: String, imageURLs: List<String>) : Long {
        val diary = diaryRepository.save(
            Diary(
                Title(title),
                Content(content),
                Place("루터회관", "서울 송파구 올림픽로35다길 42"), // FIXME : 요청에서 장소 정보를 받도록 수정해야 한다
                imageURLs,
                ShareScope.valueOf(shareScope)
            )
        )
        return diary.id ?: throw IllegalArgumentException("저장한 일기장의 식별자를 찾을 수 없습니다.")
    }

    fun uploadImages(images: List<MultipartFile>) : List<String> {

        val imageRequests = images.map { image ->
            ImageUploadingRequest(
                image,
                "DIARY_${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}",
                ImageCategory.DIARY
            )
        }.toMutableList()

        return imageService.uploadImage(imageRequests)
    }
}
