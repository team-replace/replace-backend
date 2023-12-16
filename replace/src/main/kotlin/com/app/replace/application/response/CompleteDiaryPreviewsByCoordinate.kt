package com.app.replace.application.response

import com.app.replace.domain.Diary
import com.app.replace.domain.Place
import java.time.LocalDateTime

private const val THUMBNAILS_MAX_SIZE = 3

open class CompleteDiaryPreviewsByCoordinate(
    val place: Place,
    val coupleDiaries: List<DiaryPreviewByCoordinate>,
    override val allDiaries: List<DiaryPreviewByCoordinate>,
    override val isLast: Boolean
) : DiaryPreviewsByCoordinate(allDiaries, isLast)

open class DiaryPreviewsByCoordinate(
    open val allDiaries: List<DiaryPreviewByCoordinate>,
    open val isLast: Boolean
)

data class DiaryPreviewByCoordinate(
    val id: Long,
    val user: SimpleUserProfile,
    val title: String,
    val thumbnails: List<String>,
    val numOfExtraThumbnails: Int,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(diary: Diary, writer: Writer): DiaryPreviewByCoordinate {
            val thumbnails = diary.imageURLs.map { imageURL -> imageURL.url }.toList()

            if (thumbnails.size <= THUMBNAILS_MAX_SIZE) {
                return DiaryPreviewByCoordinate(
                    diary.id!!,
                    SimpleUserProfile(writer.nickname, writer.profileImage),
                    diary.title.title,
                    thumbnails,
                    0,
                    diary.createdAt
                )
            }
            return DiaryPreviewByCoordinate(
                diary.id!!,
                SimpleUserProfile(writer.nickname, writer.profileImage),
                diary.title.title,
                thumbnails.subList(0, THUMBNAILS_MAX_SIZE),
                if (THUMBNAILS_MAX_SIZE < thumbnails.size) thumbnails.size - THUMBNAILS_MAX_SIZE else 0,
                diary.createdAt
            )
        }
    }
}
