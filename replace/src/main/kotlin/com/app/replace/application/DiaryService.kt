package com.app.replace.application

import com.app.replace.common.exception.BadRequestException
import com.app.replace.domain.*
import com.fasterxml.jackson.annotation.JsonUnwrapped
import com.fasterxml.jackson.annotation.JsonValue
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
@Transactional
class DiaryService(
    private val diaryRepository : DiaryRepository,
    private val connectionService: ConnectionService,
    private val userService: UserService,
    private val imageService: ImageService
) {
    fun createDiary(userId: Long, title: String, content: String, shareScope: String, imageURLs: List<String>): Long {
        val diary = diaryRepository.save(
            Diary(
                Title(title),
                Content(content),
                Place("루터회관", "서울 송파구 올림픽로35다길 42"), // FIXME : 요청에서 장소 정보를 받도록 수정해야 한다
                ImageURL.from(imageURLs),
                userId,
                ShareScope.valueOf(shareScope)
            )
        )
        return diary.id ?: throw IllegalArgumentException("저장한 일기장의 식별자를 찾을 수 없습니다.")
    }

    fun updateDiary(
        userId: Long,
        diaryId: Long,
        title: String,
        content: String,
        shareScope: String,
        imageURLs: List<String>
    ) {
        val diary =
            diaryRepository.findByIdOrNull(diaryId) ?: throw IllegalArgumentException("id에 해당하는 일기장을 찾을 수 없습니다.")

        diary.update(
            Diary(
                Title(title),
                Content(content),
                Place("루터회관", "서울 송파구 올림픽로35다길 42"), // FIXME : 요청에서 장소 정보를 받도록 수정해야 한다
                ImageURL.from(imageURLs),
                userId,
                ShareScope.valueOf(shareScope)
            )
        )
    }

    fun deleteDiary(diaryId: Long) {
        diaryRepository.deleteById(diaryId)
    }

    @Transactional(readOnly = true)
    fun loadSingleDiary(id: Long): SingleDiaryRecord {
        val diary = diaryRepository.findByIdOrNull(id) ?: throw IllegalArgumentException("id에 해당하는 일기장을 찾을 수 없습니다.")
        val user = userService.loadSimpleUserInformationById(
            diary.userId ?: throw IllegalArgumentException("작성자 정보가 존재하지 않습니다.")
        )
        return SingleDiaryRecord.from(diary, user)
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

    @Transactional(readOnly = true)
    fun loadDiaries(userId: Long, date: LocalDate) : DiaryPreviews {
        if (date.isAfter(LocalDate.now())) {
            throw InvalidDateException()
        }

        val ids = mutableListOf(userId)
        val partnerId = connectionService.findPartnerIdByUserId(userId)
        if (partnerId != null) ids.add(partnerId)

        val diaryPreviews = ids.map { id -> loadDiaryPreviews(id) }.toList()
        return DiaryPreviews(diaryPreviews)
    }

    private fun loadDiaryPreviews(id: Long): DiaryPreview {
        val (nickname, imageUrl) = userService.loadSimpleUserInformationById(id)
        val writer = Writer(imageUrl, nickname)
        val contents =
            diaryRepository.findByUserIdOrderByCreatedAtDesc(id)
                .map(DiaryContentPreview.Companion::from).toList()
        return DiaryPreview(writer, contents)
    }
}

data class DiaryPreviews(
    val diaryPreviews: List<DiaryPreview>
)

data class DiaryPreview(
    val user: Writer,
    val contents: List<DiaryContentPreview>
)

data class DiaryContentPreview(
    val title: String,
    val thumbnails: List<String>,
    val numOfExtraThumbnails: Int,
    val createdAt: LocalDate
) {
    companion object {
        fun from(diary: Diary): DiaryContentPreview {
            val thumbnails = diary.imageURLs.map { url -> url.url }.toList()
            return DiaryContentPreview(
                diary.title.title,
                thumbnails.subList(0, 2),
                if (3 < thumbnails.size) thumbnails.size - 3 else 0,
                diary.createdAt.toLocalDate()
            )
        }
    }
}

data class SingleDiaryRecord(
    val id: Long,
    val images: List<ImageURLRecord>,
    val place: Place,
    val createdAt: String,
    val writer: Writer,
    @JsonUnwrapped val title: Title,
    @JsonUnwrapped val content: Content
) {
    companion object {
        fun from(diary: Diary, user: SimpleUserInformation): SingleDiaryRecord {
            return SingleDiaryRecord(
                diary.id ?: throw IllegalArgumentException("식별자가 존재하지 않는 일기장을 응답할 수 없습니다."),
                diary.imageURLs
                    .map { imageURL -> ImageURLRecord(imageURL.url) }
                    .toList(),
                diary.place,
                diary.createdAt.toString(),
                Writer(user.imageUrl, user.nickname),
                diary.title,
                diary.content
            )
        }
    }
}

data class Writer(val profileImage: String, val nickname: String)

data class ImageURLRecord(@JsonValue val imageURL: String)

class InvalidDateException(override val message: String? = "선택할 수 없는 날짜입니다.") : BadRequestException(7000)
