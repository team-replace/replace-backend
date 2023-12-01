package com.app.replace.ui

import com.app.replace.application.*
import com.app.replace.application.exception.InvalidDateException
import com.app.replace.application.response.*
import com.app.replace.domain.Content
import com.app.replace.domain.Coordinate
import com.app.replace.domain.Place
import com.app.replace.domain.Title
import com.app.replace.ui.request.CreateDiaryRequest
import com.app.replace.ui.response.ImageUploadingResponse
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.math.BigDecimal
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.time.LocalDateTime

@WebMvcTest(controllers = [DiaryController::class])
@AutoConfigureMockMvc
class DiaryControllerTest(
    @Autowired val diaryController: DiaryController,
) : MockMvcPreparingManager(diaryController) {

    @MockkBean
    lateinit var diaryService: DiaryService

    @Test
    fun `일기장을 저장하는 API를 호출한 후에는 Location 헤더에 ID를 적어 응답한다`() {
        every {
            diaryService.createDiary(
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

    @Test
    fun `커플 연결이 되지 않았을 때에는 내가 작성한 일기만 보여준다`() {
        every { diaryService.loadDiaries(1L, LocalDate.of(2023, 4, 15)) } returns DiaryPreviews(
            listOf(
                DiaryPreview(
                    Writer(
                        "https://replace-s3.s3.ap-northeast-2.amazonaws.com/client/profile/replace-default-profile-image.png",
                        "요아소비"
                    ),
                    listOf(
                        DiaryContentPreview(
                            1L,
                            "테스트 제목 1",
                            listOf(
                                "https://mybucket.s3.amazonaws.com/images/photo1.jpg",
                                "https://s3-us-west-2.amazonaws.com/mybucket/images/pic2.png",
                                "https://my-s3-bucket.s3.eu-central-1.amazonaws.com/photos/image3.jpg"
                            ),
                            4,
                            LocalDateTime.of(2023, 4, 15, 9, 56, 33, 22233)
                        ),
                        DiaryContentPreview(
                            1L,
                            "테스트 제목 2",
                            listOf(
                                "https://mybucket.s3.amazonaws.com/images/photo1.jpg",
                                "https://s3-us-west-2.amazonaws.com/mybucket/images/pic2.png",
                                "https://my-s3-bucket.s3.eu-central-1.amazonaws.com/photos/image3.jpg"
                            ),
                            4,
                            LocalDateTime.of(2023, 4, 15, 2, 1, 11, 998)
                        )
                    )
                )
            )
        )

        mockMvc.perform(
            get("/diarys")
                .param("year", "2023")
                .param("month", "4")
                .param("day", "15")
                .header(AUTHENTICATION_HEADER_NAME, "pobi")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .characterEncoding(StandardCharsets.UTF_8)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.diaries[0].user.profileImage").value("https://replace-s3.s3.ap-northeast-2.amazonaws.com/client/profile/replace-default-profile-image.png"))
            .andExpect(jsonPath("$.diaries[0].user.nickname").value("요아소비"))
            .andExpect(jsonPath("$.diaries[0].contents[0].id").value(1L))
            .andExpect(jsonPath("$.diaries[0].contents[0].title").value("테스트 제목 1"))
            .andExpect(jsonPath("$.diaries[0].contents[0].thumbnails.size()").value(3))
            .andExpect(jsonPath("$.diaries[0].contents[0].numOfExtraThumbnails").value(4))
            .andExpect(jsonPath("$.diaries[0].contents[0].createdAt").isNotEmpty)
            .andExpect(jsonPath("$.diaries[0].contents[1].title").value("테스트 제목 2"))
            .andExpect(jsonPath("$.diaries[0].contents[1].thumbnails.size()").value(3))
            .andExpect(jsonPath("$.diaries[0].contents[1].numOfExtraThumbnails").value(4))
            .andExpect(jsonPath("$.diaries[0].contents[1].createdAt").isNotEmpty)
    }

    @Test
    fun `커플 연결이 되었을 경우 내 일기 파트너 일기 순서대로 보여준다`() {
        every { diaryService.loadDiaries(1L, LocalDate.of(2023, 4, 15)) } returns DiaryPreviews(
            listOf(
                DiaryPreview(
                    Writer(
                        "https://replace-s3.s3.ap-northeast-2.amazonaws.com/client/profile/replace-default-profile-image.png",
                        "요아소비"
                    ),
                    listOf(
                        DiaryContentPreview(
                            1L,
                            "테스트 제목 1",
                            listOf(
                                "https://mybucket.s3.amazonaws.com/images/photo1.jpg",
                                "https://s3-us-west-2.amazonaws.com/mybucket/images/pic2.png",
                                "https://my-s3-bucket.s3.eu-central-1.amazonaws.com/photos/image3.jpg"
                            ),
                            4,
                            LocalDateTime.of(2023, 4, 15, 9, 56, 33, 22233)
                        ),
                        DiaryContentPreview(
                            1L,
                            "테스트 제목 2",
                            listOf(
                                "https://mybucket.s3.amazonaws.com/images/photo1.jpg",
                                "https://s3-us-west-2.amazonaws.com/mybucket/images/pic2.png",
                                "https://my-s3-bucket.s3.eu-central-1.amazonaws.com/photos/image3.jpg"
                            ),
                            4,
                            LocalDateTime.of(2023, 4, 15, 2, 1, 11, 998)
                        )
                    )
                ),
                DiaryPreview(
                    Writer(
                        "https://replace-s3.s3.ap-northeast-2.amazonaws.com/client/profile/replace-default-profile-image.png",
                        "뽀또"
                    ),
                    listOf(
                        DiaryContentPreview(
                            1L,
                            "테스트 제목 3",
                            listOf(
                                "https://mybucket.s3.amazonaws.com/images/photo1.jpg",
                                "https://s3-us-west-2.amazonaws.com/mybucket/images/pic2.png",
                                "https://my-s3-bucket.s3.eu-central-1.amazonaws.com/photos/image3.jpg"
                            ),
                            4,
                            LocalDateTime.of(2023, 4, 15, 9, 56, 33, 22233)
                        ),
                        DiaryContentPreview(
                            1L,
                            "테스트 제목 4",
                            listOf(
                                "https://mybucket.s3.amazonaws.com/images/photo1.jpg",
                                "https://s3-us-west-2.amazonaws.com/mybucket/images/pic2.png",
                                "https://my-s3-bucket.s3.eu-central-1.amazonaws.com/photos/image3.jpg"
                            ),
                            4,
                            LocalDateTime.of(2023, 4, 15, 2, 1, 11, 998)
                        )
                    )
                )
            )
        )

        mockMvc.perform(
            get("/diarys")
                .param("year", "2023")
                .param("month", "4")
                .param("day", "15")
                .header(AUTHENTICATION_HEADER_NAME, "pobi")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .characterEncoding(StandardCharsets.UTF_8)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.diaries.size()").value(2))
            .andExpect(jsonPath("$.diaries[0].user.nickname").value("요아소비"))
            .andExpect(jsonPath("$.diaries[1].user.nickname").value("뽀또"))
    }

    @Test
    fun `선택 불가능한 날짜를 입력했을 때 발생하는 예외 코드는 7000이다`() {
        every { diaryService.loadDiaries(any(), LocalDate.of(2099, 4, 15)) } throws InvalidDateException()

        mockMvc.perform(
            get("/diarys")
                .param("year", "2099")
                .param("month", "4")
                .param("day", "15")
                .header(AUTHENTICATION_HEADER_NAME, "pobi")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .characterEncoding(StandardCharsets.UTF_8)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("errorCode").value(7000))
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
            Coordinate(BigDecimal("127.103068896795"), BigDecimal("37.5152535228382")),
            listOf(
                "https://mybucket.s3.amazonaws.com/images/photo1.jpg",
                "https://s3-us-west-2.amazonaws.com/mybucket/images/pic2.png",
                "https://my-s3-bucket.s3.eu-central-1.amazonaws.com/photos/image3.jpg"
            )
        )
    )
}
