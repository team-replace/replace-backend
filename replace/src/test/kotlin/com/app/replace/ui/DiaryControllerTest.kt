package com.app.replace.ui

import com.app.replace.application.DiaryService
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@WebMvcTest
@AutoConfigureMockMvc
class DiaryControllerTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired val objectMapper: ObjectMapper,
) {
    @MockkBean
    lateinit var diaryService: DiaryService

    @Test
    @DisplayName("일기장을 저장하는 API를 호출한 후에는 Location 헤더에 ID를 적어 응답한다.")
    fun createDiary() {
        every {
            diaryService.createDiary(
                "케로의 일기",
                "케로는 이리내와 나란히 햄버거를 먹었다. 햄버거가 생각보다 맛있어 케로 혼자 4개를 먹었다.",
                "US",
                listOf(
                    "https://mybucket.s3.amazonaws.com/images/photo1.jpg",
                    "https://s3-us-west-2.amazonaws.com/mybucket/images/pic2.png",
                    "https://my-s3-bucket.s3.eu-central-1.amazonaws.com/photos/image3.jpg"
                )
            )
        } returns 1L

        mockMvc.perform(
            post("/diary")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(create_CreateDiaryRequest())
        )
            .andExpect(MockMvcResultMatchers.header().string("location", "/url/1"))
    }

    private fun create_CreateDiaryRequest(): ByteArray = objectMapper.writeValueAsBytes(
        CreateDiaryRequest(
            "케로의 일기",
            "케로는 이리내와 나란히 햄버거를 먹었다. 햄버거가 생각보다 맛있어 케로 혼자 4개를 먹었다.",
            "US",
            listOf(
                "https://mybucket.s3.amazonaws.com/images/photo1.jpg",
                "https://s3-us-west-2.amazonaws.com/mybucket/images/pic2.png",
                "https://my-s3-bucket.s3.eu-central-1.amazonaws.com/photos/image3.jpg"
            )
        )
    )
}
