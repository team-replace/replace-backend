package com.app.replace.application

import com.app.replace.domain.*
import org.springframework.stereotype.Service

@Service
class DiaryService(val diaryRepository : DiaryRepository) {
    fun createDiary(title: String, content: String, shareScope: String, imageURLs: List<String>) : Long {
        val diary = diaryRepository.save(Diary(Title(title), Content(content), imageURLs, ShareScope.valueOf(shareScope)))
        return diary.id ?: throw IllegalArgumentException("저장한 일기장의 식별자를 찾을 수 없습니다.")
    }
}
