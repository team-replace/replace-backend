package com.app.replace.domain

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import java.time.LocalDateTime

@DataJpaTest
class ImageURLRepositoryTest(
    @Autowired val imageURLRepository: ImageURLRepository,
    @Autowired val diaryRepository: DiaryRepository
) {
    @Test
    @DisplayName("일기장과의 연결이 끊어진 모든 이미지URL들을 찾는다.")
    fun findAllUnusedImages() {
        diaryRepository.save(Diary(
            Title("10월 23일, 특별한 하루"),
            Content("오늘은 정말 특별한 하루였어요. 아침에 일어나서 창문을 열어보니 시원한 바람이 불면서 가을의 냄새가 나서 기분이 좋았어요. 연인과 함께하는 하루는 항상 특별한데, 오늘은 더욱 그런 날이었어요. 함께한 아침 식사는 따뜻한 커피와 맛있는 먹거리로 가득했어요. 서로 이야기를 나누면서 웃음 소리가 가득했죠. 날씨가 좋아서 나들이를 가기로 했어요. 함께 거닐면서는 가을의 단풍이 우리를 반긴 것 같았어요. 손을 맞잡고 산책하면서 서로에게 소소한 이야기를 나누는 것이 행복했어요. 점심은 우리가 좋아하는 음식점에서 함께한 시간이었어요. 맛있는 음식과 함께하는 대화는 언제나 특별하게 느껴져요. 서로의 이야기를 듣고 나니 더 가까워진 것 같아 기쁘기도 했어요. 오후에는 함께 영화를 보기로 했어요. 어떤 영화를 볼지 고르는 것부터가 즐거웠어요. 영화 속에서 감정을 나누면서, 언제나 함께 있는 것이 행복하다는 생각이 들었어요. 하루를 마무리할 때, 함께 보낸 소중한 시간에 감사함을 느꼈어요. 이런 특별한 순간들이 계속되길 기대하면서, 오늘 같은 날을 다시 만들고 싶어졌어요. 내일이 더 기대돼요. 사랑해."),
            Place("TimeSquare", "Times Square, New York, NY 10036"),
            listOf(
                ImageURL("https://mybucket.s3.amazonaws.com/images/photo1.jpg"),
                ImageURL("https://s3-us-west-2.amazonaws.com/mybucket/images/pic2.png")
            ),
            1L,
            ShareScope.US,
            LocalDateTime.of(2023, 10, 23, 23, 10, 13, 8739)
        ))

        val imageUrls = listOf(
            ImageURL("https://example.com/s3-images/image1.jpg"),
            ImageURL("https://example.com/s3-images/image1.jpg"),
            ImageURL("https://example.com/s3-images/image1.jpg")
        )
        imageURLRepository.saveAllAndFlush(imageUrls)
        imageURLRepository.count() shouldBe 5
        imageURLRepository.findAllByDiaryIsNull() shouldHaveSize 3
    }
}
