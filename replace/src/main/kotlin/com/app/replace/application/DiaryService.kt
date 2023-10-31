package com.app.replace.application

import com.app.replace.domain.*
import org.springframework.data.repository.findByIdOrNull
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

    fun loadSingleDiary(id: Long): SingleDiaryRecord {
        val diary = diaryRepository.findByIdOrNull(id) ?: throw IllegalArgumentException("id에 해당하는 일기장을 찾을 수 없습니다.")
        return SingleDiaryRecord.from(diary)
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

data class SingleDiaryRecord(
    val images: List<String>,
    val place: Place,
    val createdAt: String,
    val writer: Writer,
    val title: Title,
    val content: Content
) {
    companion object {
        fun from(diary: Diary): SingleDiaryRecord {
            return SingleDiaryRecord(
                diary.imageURLs,
                diary.place,
                diary.createdAt.toString(),
                Writer(
                    "https://replace-s3.s3.ap-northeast-2.amazonaws.com/client/profile/replace-default-profile-image.png",
                    "요아소비"
                ), // FIXME : 작성자 정보를 저장할 수 있어야 한다
                diary.title,
                diary.content
            )
        }
    }
}

data class Writer(val profileImage: String, val nickname: String)
