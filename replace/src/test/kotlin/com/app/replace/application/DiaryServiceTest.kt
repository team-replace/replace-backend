package com.app.replace.application

import com.app.replace.domain.DiaryRepository
import com.ninjasquad.springmockk.MockkBean
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import

@DataJpaTest
@Import(DiaryService::class)
class DiaryServiceTest(
    @Autowired val diaryService: DiaryService,
    @Autowired val diaryRepository: DiaryRepository,
) {
    @MockkBean
    lateinit var imageService: ImageService

    @Test
    @DisplayName("이미지 저장에 필요한 정보를 입력해 일기를 저장할 수 있다.")
    fun createDiary() {
        val diaryId = diaryService.createDiary(
            "케로의 일기",
            "케로는 이리내와 나란히 햄버거를 먹었다. 햄버거가 생각보다 맛있어 케로 혼자 4개를 먹었다.",
            "US",
            listOf(
                "https://mybucket.s3.amazonaws.com/images/photo1.jpg",
                "https://s3-us-west-2.amazonaws.com/mybucket/images/pic2.png",
                "https://my-s3-bucket.s3.eu-central-1.amazonaws.com/photos/image3.jpg"
            )
        )

        val diaries = diaryRepository.findAll()
        diaries shouldHaveSize 1
        diaries.get(0).id shouldBe diaryId

        Assertions.assertThat(diaries.get(0))
            .extracting("imageURLs").asList()
            .hasSize(3)
    }
}
