package com.app.replace.ui

import com.app.replace.application.DiaryService
import com.app.replace.application.PlaceService
import com.app.replace.application.response.DiaryPreviewByCoordinate
import com.app.replace.application.response.DiaryPreviewsByCoordinate
import com.app.replace.application.response.SimpleUserProfile
import com.app.replace.domain.Coordinate
import com.app.replace.domain.Place
import com.app.replace.domain.PlaceWithCoordinate
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
            PlaceWithCoordinate("한국루터회관", Coordinate(BigDecimal("127.10305689374302"), BigDecimal("37.5152439826822"))),
            PlaceWithCoordinate(
                "한국루터회관 주차장",
                Coordinate(BigDecimal("127.102995850174"), BigDecimal("37.5152710660296"))
            ),
            PlaceWithCoordinate(
                "한국루터회관 전기차충전소",
                Coordinate(BigDecimal("127.103069349741"), BigDecimal("37.5152538828512"))
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
            .andExpect(jsonPath("places[0].coordinate.longitude", equalTo(127.10305689374302)))
            .andExpect(jsonPath("places[0].coordinate.latitude", equalTo(37.5152439826822)))

            .andExpect(jsonPath("places[1].spotName", equalTo("한국루터회관 주차장")))
            .andExpect(jsonPath("places[1].coordinate.longitude", equalTo(127.102995850174)))
            .andExpect(jsonPath("places[1].coordinate.latitude", equalTo(37.5152710660296)))

            .andExpect(jsonPath("places[2].spotName", equalTo("한국루터회관 전기차충전소")))
            .andExpect(jsonPath("places[2].coordinate.longitude", closeTo(127.103069349741, 0.000001)))
            .andExpect(jsonPath("places[2].coordinate.latitude", closeTo(37.5152538828512, 0.000001)));
    }

    @Test
    @DisplayName("좌표를 지정해 요청을 보내면 해당 좌표에서 작성된 모든 일기장을 조회할 수 있다.")
    fun findDiary() {
        every {
            diaryService.loadDiariesByCoordinate(
                1L,
                Coordinate(BigDecimal("127.10023101886318"), BigDecimal("37.51331105877401"))
            )
        } returns DiaryPreviewsByCoordinate(
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
            )
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
            .andExpect(jsonPath("$.coupleDiaries[0].createdAt", hasSize(7), List::class.java)) // Assuming timestamp has 7 components
            // Couple Diary 2
            .andExpect(jsonPath("$.coupleDiaries[1].id", `is`(2)))
            .andExpect(jsonPath("$.coupleDiaries[1].user.nickname", `is`("콩하나")))
            .andExpect(jsonPath("$.coupleDiaries[1].title", `is`("콩하나의 일기")))
            .andExpect(jsonPath("$.coupleDiaries[1].thumbnails", hasSize(3), List::class.java)) // Assuming 3 thumbnails
            .andExpect(jsonPath("$.coupleDiaries[1].numOfExtraThumbnails", `is`(4)))
            .andExpect(jsonPath("$.coupleDiaries[1].createdAt", hasSize(7), List::class.java)) // Assuming timestamp has 7 components
            // All Diary
            .andExpect(jsonPath("$.allDiaries[0].id", `is`(3)))
            .andExpect(jsonPath("$.allDiaries[0].user.nickname", `is`("데이비드 쿠슈너")))
            .andExpect(jsonPath("$.allDiaries[0].title", `is`("데이비드 쿠슈너의 일기")))
            .andExpect(jsonPath("$.allDiaries[0].thumbnails", hasSize(3), List::class.java)) // Assuming 3 thumbnails
            .andExpect(jsonPath("$.allDiaries[0].numOfExtraThumbnails", `is`(4)))
            .andExpect(jsonPath("$.allDiaries[0].createdAt", hasSize(7), List::class.java)) // Assuming timestamp has 7 components
    }
}
