package com.app.replace.application

import com.app.replace.application.exception.InvalidDateException
import com.app.replace.application.response.CompleteDiaryPreviewsByCoordinate
import com.app.replace.application.response.DiaryPreviewsByCoordinate
import com.app.replace.application.response.SimpleUserInformation
import com.app.replace.domain.*
import com.ninjasquad.springmockk.MockkBean
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.date.shouldHaveSameDayAs
import io.kotest.matchers.shouldBe
import io.mockk.every
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.support.TransactionTemplate
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@DataJpaTest
@Import(DiaryService::class)
class DiaryServiceTest(
    @Autowired val diaryService: DiaryService,
    @Autowired val diaryRepository: DiaryRepository,
) {
    @MockkBean
    lateinit var imageService: ImageService

    @MockkBean
    lateinit var userService: UserService

    @MockkBean
    lateinit var connectionService: ConnectionService

    @MockkBean
    lateinit var placeFinder: PlaceFinder

    @BeforeEach
    fun init() {
        every { userService.loadSimpleUserInformationById(1L) } returns SimpleUserInformation(
            "케로",
            "https://my-s3-bucket.s3.eu-central-1.amazonaws.com"
        )

        every { userService.loadSimpleUserInformationById(2L) } returns SimpleUserInformation(
            "말랑",
            "https://my-s3-bucket.s3.eu-central-1.amazonaws.com"
        )

        every { placeFinder.findPlaceByCoordinate(any(Coordinate::class)) } returns Place("루터회관", "서울 송파구 올림픽로35다길 42")

        every {
            placeFinder.zeroCoordinate(
                Coordinate(
                    BigDecimal("127.103068896795"),
                    BigDecimal("37.5152535228382")
                )
            )
        } returns Coordinate(BigDecimal("127.103068896795"), BigDecimal("37.5152535228382"))
    }

    @Test
    @DisplayName("이미지 저장에 필요한 정보를 입력해 일기를 저장할 수 있다.")
    fun createDiary() {
        val diaryId = diaryService.createDiary(
            1L,
            "케로의 일기",
            "케로는 이리내와 나란히 햄버거를 먹었다. 햄버거가 생각보다 맛있어 케로 혼자 4개를 먹었다.",
            "US",
            Coordinate(BigDecimal("127.103068896795"), BigDecimal("37.5152535228382")),
            listOf(
                "https://mybucket.s3.amazonaws.com/images/photo1.jpg",
                "https://s3-us-west-2.amazonaws.com/mybucket/images/pic2.png",
                "https://my-s3-bucket.s3.eu-central-1.amazonaws.com/photos/image3.jpg"
            )
        )

        val diaries = diaryRepository.findAll()
        diaries shouldHaveSize 1
        diaries.get(0).id shouldBe diaryId

        assertThat(diaries.get(0))
            .extracting("imageURLs").asList()
            .hasSize(3)
    }

    @Nested
    @DisplayName("게시글을 수정할 때")
    inner class UpdatingDiary(
        @Autowired val transactionTemplate: TransactionTemplate,
        @Autowired val entityManager: EntityManager
    ) {
        @Test
        @DisplayName("제목을 수정할 수 있다.")
        fun updateTitle() {
            val diaryId = `create a diary and return id`(1L)
            transactionTemplate.execute {
                diaryService.updateDiary(
                    1L,
                    diaryId,
                    "케로의 방명록",
                    "케로는 이리내와 나란히 햄버거를 먹었다. 햄버거가 생각보다 맛있어 케로 혼자 4개를 먹었다.",
                    "US",
                    listOf(
                        "https://mybucket.s3.amazonaws.com/images/photo1.jpg",
                        "https://s3-us-west-2.amazonaws.com/mybucket/images/pic2.png",
                        "https://my-s3-bucket.s3.eu-central-1.amazonaws.com/photos/image3.jpg"
                    )
                )
            }
            entityManager.flush()
            entityManager.clear()

            val result = diaryRepository.findByIdOrNull(diaryId) ?: fail()
            result.title shouldBe Title("케로의 방명록")
        }

        @Test
        @DisplayName("내용을 수정할 수 있다.")
        fun contentTitle() {
            val diaryId = `create a diary and return id`(1L)
            transactionTemplate.execute {
                diaryService.updateDiary(
                    1L,
                    diaryId,
                    "케로의 일기",
                    "케로는 롯데월드에 갔다. 그 곳에서 이리내와 결별했다.",
                    "US",
                    listOf(
                        "https://mybucket.s3.amazonaws.com/images/photo1.jpg",
                        "https://s3-us-west-2.amazonaws.com/mybucket/images/pic2.png",
                        "https://my-s3-bucket.s3.eu-central-1.amazonaws.com/photos/image3.jpg"
                    )
                )
            }
            entityManager.flush()
            entityManager.clear()

            val result = diaryRepository.findByIdOrNull(diaryId) ?: fail()
            result.content shouldBe Content("케로는 롯데월드에 갔다. 그 곳에서 이리내와 결별했다.")
        }

        @Test
        @DisplayName("이미지를 추가 저장할 수 있다.")
        fun addImage() {
            val diaryId = `create a diary and return id`(1L)
            diaryService.updateDiary(
                1L,
                diaryId,
                "케로의 일기",
                "케로는 이리내와 나란히 햄버거를 먹었다. 햄버거가 생각보다 맛있어 케로 혼자 4개를 먹었다.",
                "US",
                listOf(
                    "https://mybucket.s3.amazonaws.com/images/photo1.jpg",
                    "https://s3-us-west-2.amazonaws.com/mybucket/images/pic2.png",
                    "https://my-s3-bucket.s3.eu-central-1.amazonaws.com/photos/image3.jpg",
                    "https://additional.s3.eu-central-1.amazonaws.com/photos/image3.jpg",
                )
            )
            entityManager.flush()
            entityManager.clear()

            val result = diaryRepository.findByIdOrNull(diaryId) ?: fail()
            result.imageURLs shouldHaveSize 4
        }

        @Test
        @DisplayName("기존에 저장된 이미지를 삭제할 수 있다.")
        fun removeImage() {
            val diaryId = `create a diary and return id`(1L)
            diaryService.updateDiary(
                1L,
                diaryId,
                "케로의 일기",
                "케로는 이리내와 나란히 햄버거를 먹었다. 햄버거가 생각보다 맛있어 케로 혼자 4개를 먹었다.",
                "US",
                listOf(
                    "https://mybucket.s3.amazonaws.com/images/photo1.jpg",
                )
            )
            entityManager.flush()
            entityManager.clear()

            val result = diaryRepository.findByIdOrNull(diaryId) ?: fail()
            result.imageURLs shouldHaveSize 1
        }

        @Test
        @DisplayName("동시에 다 바꿀 수 있다.")
        fun updateAll() {
            val diaryId = `create a diary and return id`(1L)
            diaryService.updateDiary(
                1L,
                diaryId,
                "케로의 방명록",
                "케로는 오늘 아무것도 하지 않았다.",
                ShareScope.ALL.name,
                listOf(
                    "https://mybucket.s3.amazonaws.com/images/photo1.jpg",
                )
            )
            entityManager.flush()
            entityManager.clear()

            val result = diaryRepository.findByIdOrNull(diaryId) ?: fail()
            result.title shouldBe Title("케로의 방명록")
            result.content shouldBe Content("케로는 오늘 아무것도 하지 않았다.")
            result.shareScope shouldBe ShareScope.ALL
            result.imageURLs shouldHaveSize 1
        }
    }

    @Test
    @DisplayName("일기장을 삭제할 수 있다.")
    fun deleteDiary() {
        val diaryId = `create a diary and return id`(1L)
        diaryService.deleteDiary(diaryId)

        diaryRepository.findAll() shouldHaveSize 0
    }

    @Test
    fun `커플 연결이 되지 않았을 때에는 내가 작성한 일기만 보여준다`() {
        `create a diary and return id`(1L)
        `create a diary and return id`(1L)
        every { connectionService.findPartnerIdByUserId(1L) } returns null

        val diaries = diaryService.loadDiaries(1L, LocalDate.now())

        diaries.diaries shouldHaveSize 1
        diaries.diaries.get(0).contents shouldHaveSize 2
    }

    @Test
    fun `커플 연결이 되었을 경우 내 일기 파트너 일기 순서대로 보여준다`() {
        `create a diary and return id`(1L)
        `create a diary and return id`(1L)
        `create a diary and return id`(2L)
        every { connectionService.findPartnerIdByUserId(1L) } returns 2L

        val diaries = diaryService.loadDiaries(1L, LocalDate.now())

        diaries.diaries shouldHaveSize 2

        diaries.diaries.get(0).contents shouldHaveSize 2
        diaries.diaries.get(0).user.nickname shouldBe "케로"

        diaries.diaries.get(1).contents shouldHaveSize 1
        diaries.diaries.get(1).user.nickname shouldBe "말랑"
    }

    @Test
    fun `선택 불가능한 날짜를 입력했을 때 발생하는 예외 코드는 7000이다`() {
        assertThatThrownBy { diaryService.loadDiaries(1L, LocalDate.of(2099, 12, 4)) }
            .isInstanceOf(InvalidDateException::class.java)
            .hasMessage("선택할 수 없는 날짜입니다.")
            .extracting("code").isEqualTo(7000)
    }

    @Test
    fun `지정한 날짜에 작성한 일기장의 목록을 보여준다`() {
        every { connectionService.findPartnerIdByUserId(1L) } returns 2L

        // Given : 10월 23일의 일기
        diaryRepository.save(
            Diary(
                Title("10월 23일의 일기"),
                Content("안녕하세요! 오늘은 정말 즐거운 하루였어요. 아침에 일어나서 창밖으로 보니 예쁜 태양이 떠 있었어요. 하늘에는 하얀 구름들이 자유롭게 떠다니고 있었어요. 아침 식사 후에는 학교에 가는 길에 나무 아래에 떨어진 도토리를 발견했어요. 도토리를 주머니에 담아가면 선생님께 보여주면 좋은 일이 생길 거라고 친구가 말해줬어요. 그래서 기뻐서 도토리를 주머니에 넣고 학교에 갔어요. 학교에서는 새로운 친구들을 만났어요. 함께 놀이 시간에는 공원에서 즐거운 시간을 보냈어요. 미끄럼틀에서 미끄러져 내려가면서 웃음 소리가 가득했어요. 그리고 친구들과 함께 길게 뛰어놀면서 에너지를 다 쏟아냈어요. 점심 시간에는 제가 좋아하는 치킨과 샐러드를 먹었어요. 배가 부르게 먹고 나니 행복한 기분이 느껴졌어요. 그리고 수업 시간에는 새로운 것을 배우면서 더 똑똑해지고 있다는 느낌이 들었어요. 저녁에는 가족과 함께 맛있는 저녁을 먹었어요. 엄마가 만든 김치찌개는 정말 맛있었어요. 식탁에서 가족들끼리 이야기를 나누면서 하루를 마무리했어요. 이렇게 특별한 하루를 일기에 담아보았어요. 내일도 좋은 일이 가득하길 기대해봐야겠어요. 안녕!"),
                Coordinate(BigDecimal("127.103068896795"), BigDecimal("37.5152535228382")),
                listOf(
                    ImageURL("https://mybucket.s3.amazonaws.com/images/photo1.jpg"),
                    ImageURL("https://s3-us-west-2.amazonaws.com/mybucket/images/pic2.png"),
                    ImageURL("https://my-s3-bucket.s3.eu-central-1.amazonaws.com/photos/image3.jpg")
                ),
                1L,
                ShareScope.US,
                LocalDateTime.of(2023, 10, 23, 5, 23, 30, 334232)
            )
        )
        // Given : 10월 23일에 쓴 파트너의 일기
        diaryRepository.save(
            Diary(
                Title("10월 23일, 특별한 하루"),
                Content("오늘은 정말 특별한 하루였어요. 아침에 일어나서 창문을 열어보니 시원한 바람이 불면서 가을의 냄새가 나서 기분이 좋았어요. 연인과 함께하는 하루는 항상 특별한데, 오늘은 더욱 그런 날이었어요. 함께한 아침 식사는 따뜻한 커피와 맛있는 먹거리로 가득했어요. 서로 이야기를 나누면서 웃음 소리가 가득했죠. 날씨가 좋아서 나들이를 가기로 했어요. 함께 거닐면서는 가을의 단풍이 우리를 반긴 것 같았어요. 손을 맞잡고 산책하면서 서로에게 소소한 이야기를 나누는 것이 행복했어요. 점심은 우리가 좋아하는 음식점에서 함께한 시간이었어요. 맛있는 음식과 함께하는 대화는 언제나 특별하게 느껴져요. 서로의 이야기를 듣고 나니 더 가까워진 것 같아 기쁘기도 했어요. 오후에는 함께 영화를 보기로 했어요. 어떤 영화를 볼지 고르는 것부터가 즐거웠어요. 영화 속에서 감정을 나누면서, 언제나 함께 있는 것이 행복하다는 생각이 들었어요. 하루를 마무리할 때, 함께 보낸 소중한 시간에 감사함을 느꼈어요. 이런 특별한 순간들이 계속되길 기대하면서, 오늘 같은 날을 다시 만들고 싶어졌어요. 내일이 더 기대돼요. 사랑해."),
                Coordinate(BigDecimal("127.103068896795"), BigDecimal("37.5152535228382")),
                listOf(
                    ImageURL("https://mybucket.s3.amazonaws.com/images/photo1.jpg"),
                    ImageURL("https://s3-us-west-2.amazonaws.com/mybucket/images/pic2.png"),
                    ImageURL("https://my-s3-bucket.s3.eu-central-1.amazonaws.com/photos/image3.jpg")
                ),
                2L,
                ShareScope.US,
                LocalDateTime.of(2023, 10, 23, 23, 10, 13, 8739)
            )
        )

        // When
        val diaries = diaryService.loadDiaries(1L, LocalDate.of(2023, 10, 23))

        // Then
        val userDiaries = diaries.diaries.get(0)
        userDiaries.contents shouldHaveSize 1
        userDiaries.contents.get(0).createdAt.toLocalDate() shouldHaveSameDayAs LocalDate.of(2023, 10, 23)

        val partnerDiaries = diaries.diaries.get(1)
        partnerDiaries.contents shouldHaveSize 1
        partnerDiaries.contents.get(0).createdAt.toLocalDate() shouldHaveSameDayAs LocalDate.of(2023, 10, 23)
    }

    @Test
    fun `지정한 날짜에 작성하지 않은 일기장은 보여주지 않는다`() {
        every { connectionService.findPartnerIdByUserId(1L) } returns 2L

        // Given : 10월 23일의 일기
        diaryRepository.save(
            Diary(
                Title("10월 23일의 일기"),
                Content("안녕하세요! 오늘은 정말 즐거운 하루였어요. 아침에 일어나서 창밖으로 보니 예쁜 태양이 떠 있었어요. 하늘에는 하얀 구름들이 자유롭게 떠다니고 있었어요. 아침 식사 후에는 학교에 가는 길에 나무 아래에 떨어진 도토리를 발견했어요. 도토리를 주머니에 담아가면 선생님께 보여주면 좋은 일이 생길 거라고 친구가 말해줬어요. 그래서 기뻐서 도토리를 주머니에 넣고 학교에 갔어요. 학교에서는 새로운 친구들을 만났어요. 함께 놀이 시간에는 공원에서 즐거운 시간을 보냈어요. 미끄럼틀에서 미끄러져 내려가면서 웃음 소리가 가득했어요. 그리고 친구들과 함께 길게 뛰어놀면서 에너지를 다 쏟아냈어요. 점심 시간에는 제가 좋아하는 치킨과 샐러드를 먹었어요. 배가 부르게 먹고 나니 행복한 기분이 느껴졌어요. 그리고 수업 시간에는 새로운 것을 배우면서 더 똑똑해지고 있다는 느낌이 들었어요. 저녁에는 가족과 함께 맛있는 저녁을 먹었어요. 엄마가 만든 김치찌개는 정말 맛있었어요. 식탁에서 가족들끼리 이야기를 나누면서 하루를 마무리했어요. 이렇게 특별한 하루를 일기에 담아보았어요. 내일도 좋은 일이 가득하길 기대해봐야겠어요. 안녕!"),
                Coordinate(BigDecimal("127.103068896795"), BigDecimal("37.5152535228382")),
                listOf(
                    ImageURL("https://mybucket.s3.amazonaws.com/images/photo1.jpg"),
                    ImageURL("https://s3-us-west-2.amazonaws.com/mybucket/images/pic2.png"),
                    ImageURL("https://my-s3-bucket.s3.eu-central-1.amazonaws.com/photos/image3.jpg")
                ),
                1L,
                ShareScope.US,
                LocalDateTime.of(2023, 10, 23, 5, 23, 30, 334232)
            )
        )
        // Given : 10월 23일에 쓴 파트너의 일기
        diaryRepository.save(
            Diary(
                Title("10월 23일, 특별한 하루"),
                Content("오늘은 정말 특별한 하루였어요. 아침에 일어나서 창문을 열어보니 시원한 바람이 불면서 가을의 냄새가 나서 기분이 좋았어요. 연인과 함께하는 하루는 항상 특별한데, 오늘은 더욱 그런 날이었어요. 함께한 아침 식사는 따뜻한 커피와 맛있는 먹거리로 가득했어요. 서로 이야기를 나누면서 웃음 소리가 가득했죠. 날씨가 좋아서 나들이를 가기로 했어요. 함께 거닐면서는 가을의 단풍이 우리를 반긴 것 같았어요. 손을 맞잡고 산책하면서 서로에게 소소한 이야기를 나누는 것이 행복했어요. 점심은 우리가 좋아하는 음식점에서 함께한 시간이었어요. 맛있는 음식과 함께하는 대화는 언제나 특별하게 느껴져요. 서로의 이야기를 듣고 나니 더 가까워진 것 같아 기쁘기도 했어요. 오후에는 함께 영화를 보기로 했어요. 어떤 영화를 볼지 고르는 것부터가 즐거웠어요. 영화 속에서 감정을 나누면서, 언제나 함께 있는 것이 행복하다는 생각이 들었어요. 하루를 마무리할 때, 함께 보낸 소중한 시간에 감사함을 느꼈어요. 이런 특별한 순간들이 계속되길 기대하면서, 오늘 같은 날을 다시 만들고 싶어졌어요. 내일이 더 기대돼요. 사랑해."),
                Coordinate(BigDecimal("127.103068896795"), BigDecimal("37.5152535228382")),
                listOf(
                    ImageURL("https://mybucket.s3.amazonaws.com/images/photo1.jpg"),
                    ImageURL("https://s3-us-west-2.amazonaws.com/mybucket/images/pic2.png"),
                    ImageURL("https://my-s3-bucket.s3.eu-central-1.amazonaws.com/photos/image3.jpg")
                ),
                2L,
                ShareScope.US,
                LocalDateTime.of(2023, 10, 23, 23, 10, 13, 8739)
            )
        )

        // When
        val diaries = diaryService.loadDiaries(1L, LocalDate.of(2023, 10, 22))

        // Then
        val userDiaries = diaries.diaries.get(0)
        userDiaries.contents shouldHaveSize 0

        val partnerDiaries = diaries.diaries.get(1)
        partnerDiaries.contents shouldHaveSize 0
    }

    @Test
    @DisplayName("특정한 좌표에서 작성한 모든 일기장을 조회할 수 있다.")
    fun findDiariesByCoordinate() {
        every { userService.loadSimpleUserInformationById(any(Long::class)) } returns SimpleUserInformation(
            "케로",
            "https://my-s3-bucket.s3.eu-central-1.amazonaws.com"
        )

        every { connectionService.findPartnerIdByUserId(1L) } returns 2L

        `create a diary and return id`(1L)
        `create a diary and return id`(2L)
        `create a diary and return id`(3L)
        `create a diary and return id`(4L)
        `create a diary and return id`(5L)

        val result = diaryService.loadDiariesByCoordinate(
            1L,
            Coordinate(BigDecimal("127.103068896795"), BigDecimal("37.5152535228382")),
            null,
            null
        ) as CompleteDiaryPreviewsByCoordinate

        result.place shouldBe Place("루터회관", "서울 송파구 올림픽로35다길 42")
        result.coupleDiaries shouldHaveSize 2
        result.allDiaries shouldHaveSize 3
    }

    @Test
    fun `page와 size 정보를 입력하면 전체공개 일기장 정보를 페이징하여 열람할 수 있다`() {
        every { userService.loadSimpleUserInformationById(any(Long::class)) } returns SimpleUserInformation(
            "케로",
            "https://my-s3-bucket.s3.eu-central-1.amazonaws.com"
        )

        every { connectionService.findPartnerIdByUserId(1L) } returns 2L

        every {
            placeFinder.zeroCoordinate(
                Coordinate(
                    BigDecimal("127.103068896795"),
                    BigDecimal("37.5152535228382")
                )
            )
        } returns Coordinate(BigDecimal("127.103068896795"), BigDecimal("37.5152535228382"))

        for (i: Long in 2L..10L) {
            `create a diary and return id`(i)
        }

        val result = diaryService.loadDiariesByCoordinate(
            1L,
            Coordinate(BigDecimal("127.103068896795"), BigDecimal("37.5152535228382")),
            3,
            1
        )

        assertThat(result).isInstanceOf(DiaryPreviewsByCoordinate::class.java)

        result.allDiaries shouldHaveSize 3

        val allDiaries = diaryRepository.findByCoordinateOrderByCreatedAtDesc(
            Coordinate(
                BigDecimal("127.103068896795"),
                BigDecimal("37.5152535228382")
            ), PageRequest.of(0, 10)
        )
        assertThat(result.allDiaries).extracting("id").contains(
            allDiaries.content.get(3).id,
            allDiaries.content.get(4).id,
            allDiaries.content.get(5).id
        )
        assertThat(result.isLast).isFalse()
    }

    private fun `create a diary and return id`(userId: Long): Long {
        val diary = diaryService.createDiary(
            userId,
            "케로의 일기",
            "케로는 이리내와 나란히 햄버거를 먹었다. 햄버거가 생각보다 맛있어 케로 혼자 4개를 먹었다.",
            "US",
            Coordinate(BigDecimal("127.103068896795"), BigDecimal("37.5152535228382")),
            listOf(
                "https://mybucket.s3.amazonaws.com/images/photo1.jpg",
                "https://s3-us-west-2.amazonaws.com/mybucket/images/pic2.png",
                "https://my-s3-bucket.s3.eu-central-1.amazonaws.com/photos/image3.jpg"
            )
        )
        return diary
    }
}
