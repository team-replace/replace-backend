package com.app.replace.application

import com.app.replace.domain.Content
import com.app.replace.domain.DiaryRepository
import com.app.replace.domain.ShareScope
import com.app.replace.domain.Title
import com.ninjasquad.springmockk.MockkBean
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.every
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.support.TransactionTemplate
import java.time.LocalDate

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

    @BeforeEach
    fun init() {
        every { userService.loadSimpleUserInformationById(1L) } returns SimpleUserInformation(
            "케로",
            "https://my-s3-bucket.s3.eu-central-1.amazonaws.com"
        )
    }

    @Test
    @DisplayName("이미지 저장에 필요한 정보를 입력해 일기를 저장할 수 있다.")
    fun createDiary() {
        val diaryId = diaryService.createDiary(
            1L,
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
        every { userService.loadSimpleUserInformationById(2L) } returns SimpleUserInformation(
            "말랑",
            "https://my-s3-bucket.s3.eu-central-1.amazonaws.com"
        )

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
        Assertions.assertThatThrownBy { diaryService.loadDiaries(1L, LocalDate.of(2099, 12, 4)) }
            .isInstanceOf(InvalidDateException::class.java)
            .hasMessage("선택할 수 없는 날짜입니다.")
            .extracting("code").isEqualTo(7000)
    }

    private fun `create a diary and return id`(userId: Long): Long {
        val diary = diaryService.createDiary(
            userId,
            "케로의 일기",
            "케로는 이리내와 나란히 햄버거를 먹었다. 햄버거가 생각보다 맛있어 케로 혼자 4개를 먹었다.",
            "US",
            listOf(
                "https://mybucket.s3.amazonaws.com/images/photo1.jpg",
                "https://s3-us-west-2.amazonaws.com/mybucket/images/pic2.png",
                "https://my-s3-bucket.s3.eu-central-1.amazonaws.com/photos/image3.jpg"
            )
        )
        return diary
    }
}
