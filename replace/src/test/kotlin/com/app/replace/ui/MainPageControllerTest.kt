package com.app.replace.ui

import com.app.replace.application.DiaryService
import com.app.replace.application.PlaceService
import com.app.replace.application.response.DiaryPreviewByCoordinate
import com.app.replace.application.response.CompleteDiaryPreviewsByCoordinate
import com.app.replace.application.response.DiaryPreviewsByCoordinate
import com.app.replace.application.response.SimpleUserProfile
import com.app.replace.domain.Coordinate
import com.app.replace.domain.Place
import com.app.replace.domain.PlaceWithCoordinate
import com.app.replace.ui.response.CoordinateResponse
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.math.BigDecimal
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime

@WebMvcTest(controllers = [MainPageController::class])
@AutoConfigureMockMvc
class MainPageControllerTest(
    @Autowired val mainPageController: MainPageController
) : MockMvcPreparingManager(mainPageController) {

    @MockkBean
    lateinit var placeService: PlaceService

    @MockkBean
    lateinit var diaryService: DiaryService

    @Test
    @DisplayName("검색할 키워드, 현재 위치와 페이징 옵션을 제공하여 장소 정보를 얻을 수 있다.")
    fun search() {
        every {
            placeService.searchPlaceByKeyword(
                "루터회관",
                Coordinate(BigDecimal("127.10023101886318"), BigDecimal("37.51331105877401")),
                1,
                10
            )
        } returns listOf(
            PlaceWithCoordinate(
                "한국루터회관",
                CoordinateResponse.from(Coordinate(BigDecimal("127.10305689374302"), BigDecimal("37.5152439826822")))
            ),
            PlaceWithCoordinate(
                "한국루터회관 주차장",
                CoordinateResponse.from(Coordinate(BigDecimal("127.102995850174"), BigDecimal("37.5152710660296")))
            ),
            PlaceWithCoordinate(
                "한국루터회관 전기차충전소",
                CoordinateResponse.from(Coordinate(BigDecimal("127.103069349741"), BigDecimal("37.5152538828512")))
            )
        )

        mockMvc.perform(
            MockMvcRequestBuilders.get("/search?longitude=127.10023101886318&latitude=37.51331105877401&query=루터회관&page=1&size=10")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .characterEncoding(StandardCharsets.UTF_8)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("places[0].spotName", equalTo("한국루터회관")))
            .andExpect(jsonPath("places[0].coordinate.longitude", equalTo("127.10305689374302")))
            .andExpect(jsonPath("places[0].coordinate.latitude", equalTo("37.5152439826822")))

            .andExpect(jsonPath("places[1].spotName", equalTo("한국루터회관 주차장")))
            .andExpect(jsonPath("places[1].coordinate.longitude", equalTo("127.102995850174")))
            .andExpect(jsonPath("places[1].coordinate.latitude", equalTo("37.5152710660296")))

            .andExpect(jsonPath("places[2].spotName", equalTo("한국루터회관 전기차충전소")))
            .andExpect(jsonPath("places[2].coordinate.longitude", equalTo("127.103069349741")))
            .andExpect(jsonPath("places[2].coordinate.latitude", equalTo("37.5152538828512")))
    }

    @Test
    @DisplayName("좌표를 지정해 요청을 보내면 해당 좌표에서 작성된 모든 일기장을 조회할 수 있다.")
    fun findDiary() {
        every {
            diaryService.loadDiariesByCoordinate(
                1L,
                Coordinate(BigDecimal("127.10023101886318"), BigDecimal("37.51331105877401")),
                null,
                null
            )
        } returns CompleteDiaryPreviewsByCoordinate(
            Place("루터회관", "서울 송파구 올림픽로35다길 42"),
            listOf(
                DiaryPreviewByCoordinate(
                    1L,
                    SimpleUserProfile(
                        "크롱",
                        "https://replace-s3.s3.ap-northeast-2.amazonaws.com/client/profile/replace-default-profile-image.png"
                    ),
                    "크롱의 일기",
                    listOf(
                        "https://replace-s3.s3.ap-northeast-2.amazonaws.com/client/profile/replace-default-profile-image.png",
                        "https://replace-s3.s3.ap-northeast-2.amazonaws.com/client/profile/replace-default-profile-image.png",
                        "https://replace-s3.s3.ap-northeast-2.amazonaws.com/client/profile/replace-default-profile-image.png"
                    ),
                    4,
                    LocalDateTime.now()
                ),
                DiaryPreviewByCoordinate(
                    2L,
                    SimpleUserProfile(
                        "콩하나",
                        "https://replace-s3.s3.ap-northeast-2.amazonaws.com/client/profile/replace-default-profile-image.png"
                    ),
                    "콩하나의 일기",
                    listOf(
                        "https://replace-s3.s3.ap-northeast-2.amazonaws.com/client/profile/replace-default-profile-image.png",
                        "https://replace-s3.s3.ap-northeast-2.amazonaws.com/client/profile/replace-default-profile-image.png",
                        "https://replace-s3.s3.ap-northeast-2.amazonaws.com/client/profile/replace-default-profile-image.png"
                    ),
                    4,
                    LocalDateTime.now()
                ),
            ),
            listOf(
                DiaryPreviewByCoordinate(
                    3L,
                    SimpleUserProfile(
                        "데이비드 쿠슈너",
                        "https://replace-s3.s3.ap-northeast-2.amazonaws.com/client/profile/replace-default-profile-image.png"
                    ),
                    "데이비드 쿠슈너의 일기",
                    listOf(
                        "https://replace-s3.s3.ap-northeast-2.amazonaws.com/client/profile/replace-default-profile-image.png",
                        "https://replace-s3.s3.ap-northeast-2.amazonaws.com/client/profile/replace-default-profile-image.png",
                        "https://replace-s3.s3.ap-northeast-2.amazonaws.com/client/profile/replace-default-profile-image.png"
                    ),
                    4,
                    LocalDateTime.now()
                )
            ),
            true
        )

        val result = mockMvc.perform(
            MockMvcRequestBuilders.get("/map")
                .param("longitude", "127.10023101886318")
                .param("latitude", "37.51331105877401")
                .header("temporary", "pobi")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .characterEncoding(StandardCharsets.UTF_8)

        )

        // Verify the status code is 200 OK
        result.andExpect(status().isOk())

        // Verify the response JSON matches the expected structure
        result.andExpect(jsonPath("$.place.spotName", `is`("루터회관")))
            .andExpect(jsonPath("$.place.roadAddress", `is`("서울 송파구 올림픽로35다길 42")))
            .andExpect(jsonPath("$.coupleDiaries", hasSize(2), List::class.java)) // Assuming 2 couple diaries
            .andExpect(jsonPath("$.allDiaries", hasSize(1), List::class.java)) // Assuming 1 all diary
            // Couple Diary 1
            .andExpect(jsonPath("$.coupleDiaries[0].id", `is`(1)))
            .andExpect(jsonPath("$.coupleDiaries[0].user.nickname", `is`("크롱")))
            .andExpect(jsonPath("$.coupleDiaries[0].title", `is`("크롱의 일기")))
            .andExpect(jsonPath("$.coupleDiaries[0].thumbnails", hasSize(3), List::class.java)) // Assuming 3 thumbnails
            .andExpect(jsonPath("$.coupleDiaries[0].numOfExtraThumbnails", `is`(4)))
            .andExpect(
                jsonPath(
                    "$.coupleDiaries[0].createdAt",
                    hasSize(7),
                    List::class.java
                )
            ) // Assuming timestamp has 7 components
            // Couple Diary 2
            .andExpect(jsonPath("$.coupleDiaries[1].id", `is`(2)))
            .andExpect(jsonPath("$.coupleDiaries[1].user.nickname", `is`("콩하나")))
            .andExpect(jsonPath("$.coupleDiaries[1].title", `is`("콩하나의 일기")))
            .andExpect(jsonPath("$.coupleDiaries[1].thumbnails", hasSize(3), List::class.java)) // Assuming 3 thumbnails
            .andExpect(jsonPath("$.coupleDiaries[1].numOfExtraThumbnails", `is`(4)))
            .andExpect(
                jsonPath(
                    "$.coupleDiaries[1].createdAt",
                    hasSize(7),
                    List::class.java
                )
            ) // Assuming timestamp has 7 components
            // All Diary
            .andExpect(jsonPath("$.allDiaries[0].id", `is`(3)))
            .andExpect(jsonPath("$.allDiaries[0].user.nickname", `is`("데이비드 쿠슈너")))
            .andExpect(jsonPath("$.allDiaries[0].title", `is`("데이비드 쿠슈너의 일기")))
            .andExpect(jsonPath("$.allDiaries[0].thumbnails", hasSize(3), List::class.java)) // Assuming 3 thumbnails
            .andExpect(jsonPath("$.allDiaries[0].numOfExtraThumbnails", `is`(4)))
            .andExpect(
                jsonPath(
                    "$.allDiaries[0].createdAt",
                    hasSize(7),
                    List::class.java
                )
            )
            .andExpect(jsonPath("$.isLast").value(true))
    }

    @Test
    @DisplayName("로그인하지 않은 채로 요청을 보내면 '우리의 일기장' 항목을 빈 리스트로 하여 응답한다.")
    fun findDiaryWithoutLogin() {
        every {
            diaryService.loadDiariesByCoordinate(
                null,
                Coordinate(BigDecimal("127.10023101886318"), BigDecimal("37.51331105877401")),
                null,
                null
            )
        } returns CompleteDiaryPreviewsByCoordinate(
            Place("루터회관", "서울 송파구 올림픽로35다길 42"),
            listOf(),
            listOf(
                DiaryPreviewByCoordinate(
                    3L,
                    SimpleUserProfile(
                        "데이비드 쿠슈너",
                        "https://replace-s3.s3.ap-northeast-2.amazonaws.com/client/profile/replace-default-profile-image.png"
                    ),
                    "데이비드 쿠슈너의 일기",
                    listOf(
                        "https://replace-s3.s3.ap-northeast-2.amazonaws.com/client/profile/replace-default-profile-image.png",
                        "https://replace-s3.s3.ap-northeast-2.amazonaws.com/client/profile/replace-default-profile-image.png",
                        "https://replace-s3.s3.ap-northeast-2.amazonaws.com/client/profile/replace-default-profile-image.png"
                    ),
                    4,
                    LocalDateTime.now()
                )
            ),
            true
        )

        val result = mockMvc.perform(
            MockMvcRequestBuilders.get("/map")
                .param("longitude", "127.10023101886318")
                .param("latitude", "37.51331105877401")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .characterEncoding(StandardCharsets.UTF_8)

        )

        // Verify the status code is 200 OK
        result.andExpect(status().isOk())

        // Verify the response JSON matches the expected structure
        result.andExpect(jsonPath("$.place.spotName", `is`("루터회관")))
            .andExpect(jsonPath("$.place.roadAddress", `is`("서울 송파구 올림픽로35다길 42")))
            .andExpect(jsonPath("$.coupleDiaries", hasSize(0), List::class.java)) // Assuming 2 couple diaries
            .andExpect(jsonPath("$.allDiaries", hasSize(1), List::class.java)) // Assuming 1 all diary
            // All Diary
            .andExpect(jsonPath("$.allDiaries[0].id", `is`(3)))
            .andExpect(jsonPath("$.allDiaries[0].user.nickname", `is`("데이비드 쿠슈너")))
            .andExpect(jsonPath("$.allDiaries[0].title", `is`("데이비드 쿠슈너의 일기")))
            .andExpect(jsonPath("$.allDiaries[0].thumbnails", hasSize(3), List::class.java)) // Assuming 3 thumbnails
            .andExpect(jsonPath("$.allDiaries[0].numOfExtraThumbnails", `is`(4)))
            .andExpect(
                jsonPath(
                    "$.allDiaries[0].createdAt",
                    hasSize(7),
                    List::class.java
                )
            )
            .andExpect(jsonPath("$.isLast").value(true))
    }

    @Test
    @DisplayName("일기장이 작성된 모든 장소의 좌표 목록을 읽어올 수 있다.")
    fun findCoordinatesHavingDiaries() {
        every {
            diaryService.loadAllCoordinatesHavingDiary()
        } returns listOf(
            Coordinate(BigDecimal("77.0365"), BigDecimal("38.8977")),
            Coordinate(BigDecimal("126.9769"), BigDecimal("37.5816"))
        )

        val result = mockMvc.perform(
            MockMvcRequestBuilders.get("/map/coordinate")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .characterEncoding(StandardCharsets.UTF_8)
        )

        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.diaryCoordinates").isArray())
            .andExpect(
                jsonPath(
                    "$.diaryCoordinates[0].longitude",
                    equalTo(BigDecimal("77.0365")),
                    BigDecimal::class.java
                )
            )
            .andExpect(
                jsonPath(
                    "$.diaryCoordinates[0].latitude",
                    equalTo(BigDecimal("38.8977")),
                    BigDecimal::class.java
                )
            )
            .andExpect(
                jsonPath(
                    "$.diaryCoordinates[1].longitude",
                    equalTo(BigDecimal("126.9769")),
                    BigDecimal::class.java
                )
            )
            .andExpect(
                jsonPath(
                    "$.diaryCoordinates[1].latitude",
                    equalTo(BigDecimal("37.5816")),
                    BigDecimal::class.java
                )
            )
    }

    @Test
    fun `page와 size정보를 제공하면 전체공유 일기장 정보를 페이징하여 보여준다`() {
        every {
            diaryService.loadDiariesByCoordinate(
                null,
                Coordinate(BigDecimal("127.10023101886318"), BigDecimal("37.51331105877401")),
                3,
                1
            )
        } returns DiaryPreviewsByCoordinate(
            listOf(
                DiaryPreviewByCoordinate(
                    3L,
                    SimpleUserProfile(
                        "데이비드 쿠슈너",
                        "https://replace-s3.s3.ap-northeast-2.amazonaws.com/client/profile/replace-default-profile-image.png"
                    ),
                    "데이비드 쿠슈너의 일기",
                    listOf(
                        "https://replace-s3.s3.ap-northeast-2.amazonaws.com/client/profile/replace-default-profile-image.png",
                        "https://replace-s3.s3.ap-northeast-2.amazonaws.com/client/profile/replace-default-profile-image.png",
                        "https://replace-s3.s3.ap-northeast-2.amazonaws.com/client/profile/replace-default-profile-image.png"
                    ),
                    4,
                    LocalDateTime.now()
                ),
                DiaryPreviewByCoordinate(
                    4L,
                    SimpleUserProfile(
                        "킹누",
                        "https://replace-s3.s3.ap-northeast-2.amazonaws.com/client/profile/replace-default-profile-image.png"
                    ),
                    "킹누의 일기",
                    listOf(
                        "https://replace-s3.s3.ap-northeast-2.amazonaws.com/client/profile/replace-default-profile-image.png",
                        "https://replace-s3.s3.ap-northeast-2.amazonaws.com/client/profile/replace-default-profile-image.png",
                        "https://replace-s3.s3.ap-northeast-2.amazonaws.com/client/profile/replace-default-profile-image.png"
                    ),
                    4,
                    LocalDateTime.now()
                ),
                DiaryPreviewByCoordinate(
                    5L,
                    SimpleUserProfile(
                        "바운디",
                        "https://replace-s3.s3.ap-northeast-2.amazonaws.com/client/profile/replace-default-profile-image.png"
                    ),
                    "바운디의 일기",
                    listOf(
                        "https://replace-s3.s3.ap-northeast-2.amazonaws.com/client/profile/replace-default-profile-image.png",
                        "https://replace-s3.s3.ap-northeast-2.amazonaws.com/client/profile/replace-default-profile-image.png",
                        "https://replace-s3.s3.ap-northeast-2.amazonaws.com/client/profile/replace-default-profile-image.png"
                    ),
                    4,
                    LocalDateTime.now()
                )
            ),
            true
        )

        val result = mockMvc.perform(
            MockMvcRequestBuilders.get("/map")
                .param("longitude", "127.10023101886318")
                .param("latitude", "37.51331105877401")
                .param("size", "3")
                .param("page", "1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .characterEncoding(StandardCharsets.UTF_8)
        )

        result.andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print())
            .andExpect(jsonPath("$.allDiaries[0].id").value(3))
            .andExpect(jsonPath("$.allDiaries[0].user.nickname").value("데이비드 쿠슈너"))
            .andExpect(jsonPath("$.allDiaries[0].title").value("데이비드 쿠슈너의 일기"))
            .andExpect(jsonPath("$.allDiaries[0].thumbnails[0]").value("https://replace-s3.s3.ap-northeast-2.amazonaws.com/client/profile/replace-default-profile-image.png"))
            .andExpect(jsonPath("$.allDiaries[0].numOfExtraThumbnails").value(4))

            .andExpect(jsonPath("$.allDiaries[1].id").value(4))
            .andExpect(jsonPath("$.allDiaries[1].user.nickname").value("킹누"))
            .andExpect(jsonPath("$.allDiaries[1].title").value("킹누의 일기"))
            .andExpect(jsonPath("$.allDiaries[1].thumbnails[0]").value("https://replace-s3.s3.ap-northeast-2.amazonaws.com/client/profile/replace-default-profile-image.png"))
            .andExpect(jsonPath("$.allDiaries[1].numOfExtraThumbnails").value(4))

            .andExpect(jsonPath("$.allDiaries[2].id").value(5))
            .andExpect(jsonPath("$.allDiaries[2].user.nickname").value("바운디"))
            .andExpect(jsonPath("$.allDiaries[2].title").value("바운디의 일기"))
            .andExpect(jsonPath("$.allDiaries[2].thumbnails[0]").value("https://replace-s3.s3.ap-northeast-2.amazonaws.com/client/profile/replace-default-profile-image.png"))
            .andExpect(jsonPath("$.allDiaries[2].numOfExtraThumbnails").value(4))
            .andExpect(jsonPath("$.isLast").value(true))
    }
}
