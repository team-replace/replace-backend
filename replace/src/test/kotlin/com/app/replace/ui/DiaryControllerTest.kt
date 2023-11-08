package com.app.replace.ui

import com.app.replace.application.*
import com.app.replace.domain.Content
import com.app.replace.domain.Place
import com.app.replace.domain.Title
import com.app.replace.domain.UserRepository
import com.app.replace.ui.argumentresolver.AuthenticationArgumentResolver
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder
import org.springframework.web.filter.CharacterEncodingFilter
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime

@WebMvcTest
@AutoConfigureMockMvc
class DiaryControllerTest(
    @Autowired val objectMapper: ObjectMapper,
    @Autowired val diaryController: DiaryController,
    @Autowired val authenticationInterceptor: AuthenticationInterceptor,
    @Autowired val authenticationArgumentResolver: AuthenticationArgumentResolver
) {
    @MockkBean
    lateinit var diaryService: DiaryService

    @MockkBean
    lateinit var userRepository: UserRepository

    lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(diaryController)
            .addFilter<StandaloneMockMvcBuilder?>(CharacterEncodingFilter(StandardCharsets.UTF_8.name(), true))
            .addInterceptors(authenticationInterceptor)
            .setCustomArgumentResolvers(authenticationArgumentResolver)
            .build()

        every { userRepository.findIdByNickname(any()) } returns 1L
    }

    @Test
    fun `일기장을 저장하는 API를 호출한 후에는 Location 헤더에 ID를 적어 응답한다`() {
        every {
            diaryService.createDiary(
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
        } returns 1L

        mockMvc.perform(
            post("/diary")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHENTICATION_HEADER_NAME, "pobi")
                .content(create_CreateDiaryRequest())
        )
            .andExpect(header().string("location", "/url/1"))
    }

    @Test
    fun `일기에 사용할 이미지를 저장하는 API를 호출하면 저장된 이미지의 URL을 적어 응답한다`() {
        val mockMultipartFile =
            MockMultipartFile("images", "imageFile.jpg", MediaType.IMAGE_JPEG_VALUE, "<<사진>>".byteInputStream())
        val mockMultipartFile2 =
            MockMultipartFile("images", "imageFile.jpg", MediaType.IMAGE_JPEG_VALUE, "<<사진>>".byteInputStream())

        every {
            diaryService.uploadImages(listOf(mockMultipartFile, mockMultipartFile2))
        } returns listOf(
            "https://mybucket.s3.amazonaws.com/images/photo1.jpg",
            "https://s3-us-west-2.amazonaws.com/mybucket/images/pic2.png"
        )

        val imageUploadingResponse = ImageUploadingResponse(
            listOf(
                "https://mybucket.s3.amazonaws.com/images/photo1.jpg",
                "https://s3-us-west-2.amazonaws.com/mybucket/images/pic2.png"
            )
        )

        mockMvc.perform(
            multipart("/diary/images")
                .file(mockMultipartFile).file(mockMultipartFile2)
        )
            .andExpect(status().isCreated)
            .andExpect(content().string(objectMapper.writeValueAsString(imageUploadingResponse)))
    }

    @Test
    fun `아이디에 해당하는 일기장을 찾아 응답한다`() {
        every {
            diaryService.loadSingleDiary(1L)
        } returns create_singleDiaryRecord()

        mockMvc.perform(
            get("/diary/{diaryId}", 1L)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .characterEncoding(StandardCharsets.UTF_8)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(content().string(objectMapper.writeValueAsString(create_singleDiaryRecord())))
    }

    private fun create_singleDiaryRecord() = SingleDiaryRecord(
        1L,
        listOf(
            "https://mybucket.s3.amazonaws.com/images/photo1.jpg",
            "https://s3-us-west-2.amazonaws.com/mybucket/images/pic2.png",
            "https://my-s3-bucket.s3.eu-central-1.amazonaws.com/photos/image3.jpg"
        ).map { imageUrl -> ImageURLRecord(imageUrl) }.toList(),
        Place("루터회관", "서울 송파구 올림픽로35다길 42"),
        LocalDateTime.of(2023, 10, 13, 4, 1, 34, 334).toString(),
        Writer(
            "https://replace-s3.s3.ap-northeast-2.amazonaws.com/client/profile/replace-default-profile-image.png",
            "요아소비"
        ),
        Title("케로의 일기"),
        Content("케로는 이리내와 나란히 햄버거를 먹었다. 햄버거가 생각보다 맛있어 케로 혼자 4개를 먹었다.")
    )

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
