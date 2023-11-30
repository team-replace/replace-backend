package com.app.replace.application.response

import com.app.replace.domain.Diary
import java.time.LocalDateTime

private const val THUMBNAILS_MAX_SIZE = 3

data class DiaryPreviews(
    val diaries: List<DiaryPreview>
)

data class DiaryPreview(
    val user: Writer,
    val contents: List<DiaryContentPreview>
)

data class DiaryContentPreview(
    val id: Long,
    val title: String,
    val thumbnails: List<String>,
    val numOfExtraThumbnails: Int,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(diary: Diary): DiaryContentPreview {
            val thumbnails = diary.imageURLs.map { url -> url.url }.toList()

            if (thumbnails.size <= THUMBNAILS_MAX_SIZE) {
                return DiaryContentPreview(
                    diary.id!!,
                    diary.title.title,
                    thumbnails,
                    0,
                    diary.createdAt
                )
            }
            return DiaryContentPreview(
                diary.id!!,
                diary.title.title,
                thumbnails.subList(0, THUMBNAILS_MAX_SIZE),
                if (THUMBNAILS_MAX_SIZE < thumbnails.size) thumbnails.size - THUMBNAILS_MAX_SIZE else 0,
                diary.createdAt
            )
        }
    }
}
