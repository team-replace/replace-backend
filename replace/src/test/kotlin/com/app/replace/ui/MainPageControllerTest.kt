package com.app.replace.ui

import com.app.replace.application.PlaceService
import com.app.replace.domain.Coordinate
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import java.math.BigDecimal
import java.nio.charset.StandardCharsets

@WebMvcTest(controllers = [MainPageController::class])
@AutoConfigureMockMvc
class MainPageControllerTest(
    @Autowired val mainPageController: MainPageController
) : MockMvcPreparingManager(mainPageController) {

    @MockkBean
    lateinit var placeService: PlaceService

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
            .andExpect(MockMvcResultMatchers.status().isOk)
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
}
