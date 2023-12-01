package com.app.replace.application

import com.app.replace.application.exception.InvalidDateException
import com.app.replace.application.request.ImageUploadingRequest
import com.app.replace.application.response.*
import com.app.replace.domain.*
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
    private val imageService: ImageService,
    private val placeFinder: PlaceFinder
) {
    fun createDiary(
        userId: Long,
        title: String,
        content: String,
        shareScope: String,
        coordinate: Coordinate,
        imageURLs: List<String>
    ): Long {
        val diary = diaryRepository.save(
            Diary(
                Title(title),
                Content(content),
                coordinate,
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
                diary.coordinate,
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
        return SingleDiaryRecord.from(diary, user, placeFinder)
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

        val diaryPreviews = ids.map { id -> loadDiaryPreviews(id, date) }.toList()
        return DiaryPreviews(diaryPreviews)
    }

    private fun loadDiaryPreviews(id: Long, date: LocalDate): DiaryPreview {
        val (nickname, imageUrl) = userService.loadSimpleUserInformationById(id)
        val writer = Writer(imageUrl, nickname)
        val contents =
            diaryRepository.findByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(
                id,
                date.atStartOfDay(),
                date.plusDays(1).atStartOfDay()
            )
                .map(DiaryContentPreview.Companion::from).toList()
        return DiaryPreview(writer, contents)
    }
}


