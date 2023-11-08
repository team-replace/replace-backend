package com.app.replace.application

import com.app.replace.domain.Content
import com.app.replace.domain.Place
import com.app.replace.domain.Title
import com.app.replace.ui.CreateDiaryRequest
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.restassured.RestAssured
import io.restassured.http.Header
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthenticationTest(
    @Autowired val objectMapper: ObjectMapper,
    @LocalServerPort val port: Int
) {
    @MockkBean
    lateinit var diaryService: DiaryService

    @BeforeEach
    fun setUp() {
        RestAssured.port = port
    }

    @Test
    fun `인증 정보를 요구하는 요청에 인증되지 않은 요청을 보내면 예외가 발생한다`() {
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

        val responseBody = Given {
            contentType(MediaType.APPLICATION_JSON_VALUE)
            body(`create a createDiaryRequest`())
        } When {
            post("/diary")
        } Then {
            statusCode(HttpStatus.UNAUTHORIZED.value())
        } Extract {
            print(body().asString())
            body().jsonPath()
        }

        responseBody.getInt("errorCode") shouldBe 0
        responseBody.getString("errorBody") shouldBe "인증되지 않은 요청으로 접근할 수 없습니다."
    }

    @Test
    fun `인증 정보가 유효한 요청을 보내면 요청에 성공한다`() {
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

        Given {
            contentType(MediaType.APPLICATION_JSON_VALUE)
            header(Header(AUTHENTICATION_HEADER_NAME, "pobi"))
            body(`create a createDiaryRequest`())
        } When {
            post("/diary")
        } Then {
            statusCode(HttpStatus.CREATED.value())
        }
    }

    @Test
    fun `인증 정보가 불필요한 요청은 인증 정보가 없더라도 성공한다`() {
        every {
            diaryService.loadSingleDiary(any())
        } returns SingleDiaryRecord(
            1L,
            listOf(
                ImageURLRecord("https://mybucket.s3.amazonaws.com/images/photo1.jpg"),
                ImageURLRecord("https://s3-us-west-2.amazonaws.com/mybucket/images/pic2.png"),
                ImageURLRecord("https://my-s3-bucket.s3.eu-central-1.amazonaws.com/photos/image3.jpg")
            ),
            Place("서울숲", "서울특별시 중구 서소문로 124 씨티스퀘어 8층"),
            LocalDateTime.now().toString(),
            Writer("https://my-s3-bucket.s3.eu-central-1.amazonaws.com", "케로"),
            Title("케로의 일기"),
            Content("케로는 이리내와 나란히 햄버거를 먹었다. 햄버거가 생각보다 맛있어 케로 혼자 4개를 먹었다.")
        )

        Given {
            contentType(MediaType.APPLICATION_JSON_VALUE)
            body(`create a createDiaryRequest`())
        } When {
            get("/diary/1")
        } Then {
            statusCode(HttpStatus.OK.value())
        }
    }

    private fun `create a createDiaryRequest`(): ByteArray = objectMapper.writeValueAsBytes(
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
