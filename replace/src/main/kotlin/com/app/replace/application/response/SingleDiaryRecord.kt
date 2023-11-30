package com.app.replace.application.response

import com.app.replace.domain.Content
import com.app.replace.domain.Diary
import com.app.replace.domain.Place
import com.app.replace.domain.Title
import com.fasterxml.jackson.annotation.JsonUnwrapped

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
