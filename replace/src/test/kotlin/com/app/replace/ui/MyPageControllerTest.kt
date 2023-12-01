package com.app.replace.ui

import com.app.replace.application.*
import com.app.replace.application.response.AloneUserInformation
import com.app.replace.application.response.SimpleUserProfile
import com.app.replace.application.response.UserInformationWithPartner
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath

@WebMvcTest(controllers = [MyPageController::class])
@AutoConfigureMockMvc
class MyPageControllerTest(
    @Autowired val myPageController: MyPageController
) : MockMvcPreparingManager(myPageController) {

    @MockkBean lateinit var userService: UserService

    @Test
    fun `파트너가 있는 회원이 마이페이지에 접속하면 파트너와 회원의 정보를 보여준다`() {
        every {
            userService.loadUserInformationWithPartner(any())
        } returns UserInformationWithPartner(
            SimpleUserProfile("user", "https://mybucket.s3.amazonaws.com/images/photo1.jpg"),
            SimpleUserProfile("user2", "https://mybucket.s3.amazonaws.com/images/photo1.jpg")
        )

        mockMvc.perform(
            MockMvcRequestBuilders.get("/my")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHENTICATION_HEADER_NAME, "pobi")
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(jsonPath("$.user.nickname").value("user"))
            .andExpect(jsonPath("$.user.profileImage").value("https://mybucket.s3.amazonaws.com/images/photo1.jpg"))
            .andExpect(jsonPath("$.partner.nickname").value("user2"))
            .andExpect(jsonPath("$.partner.profileImage").value("https://mybucket.s3.amazonaws.com/images/photo1.jpg"));
    }

    @Test
    fun `파트너가 없는 회원이 마이페이지에 접속하면 회원의 정보만 보여준다`() {
        every {
            userService.loadUserInformationWithPartner(any())
        } returns AloneUserInformation(
            SimpleUserProfile("user", "https://mybucket.s3.amazonaws.com/images/photo1.jpg"),
        )

        mockMvc.perform(
            MockMvcRequestBuilders.get("/my")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHENTICATION_HEADER_NAME, "pobi")
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(jsonPath("$.user.nickname").value("user"))
            .andExpect(jsonPath("$.user.profileImage").value("https://mybucket.s3.amazonaws.com/images/photo1.jpg"))
            .andExpect(jsonPath("$.partner").doesNotExist())
    }
}

