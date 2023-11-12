package com.app.replace.ui

import com.app.replace.application.*
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

@WebMvcTest(controllers = [ConnectionController::class])
@AutoConfigureMockMvc
class ConnectionControllerTest(
    @Autowired private val connectionController: ConnectionController
) : MockMvcPreparingManager(connectionController) {

    @MockkBean
    lateinit var connectionService: ConnectionService

    @Test
    fun `나에게 고유하게 할당된 코드를 가져올 수 있다`() {
        val mockCode = UUID.randomUUID().toString()
        every { connectionService.loadConnection(any()) } returns mockCode

        mockMvc.perform(
            MockMvcRequestBuilders.get("/connection/code")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHENTICATION_HEADER_NAME, "pobi")
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("code").value(mockCode))
    }

    @Test
    fun `회원과 회원을 연결하여 커플을 맺을 수 있다`() {
        every { connectionService.makeConnection(any(), any()) } just runs

        val requestBody = objectMapper.writeValueAsString(MakingConnectionRequest(UUID.randomUUID().toString()))

        mockMvc.perform(
            MockMvcRequestBuilders.post("/connection/code")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody)
                .header(AUTHENTICATION_HEADER_NAME, "pobi")
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
    }

    @Test
    fun `존재하지 않는 코드를 입력하여 발생하는 오류 코드는 5000번이다`() {
        every { connectionService.makeConnection(any(), any()) } throws ConnectionCodeNotFoundException()

        val requestBody = objectMapper.writeValueAsString(MakingConnectionRequest(UUID.randomUUID().toString()))

        mockMvc.perform(
            MockMvcRequestBuilders.post("/connection/code")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody)
                .header(AUTHENTICATION_HEADER_NAME, "pobi")
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("errorCode").value(5000))
    }

    @Test
    fun `이미 다른 사람과 연결된 코드를 입력하려 했을 때 발생하는 오류 코드는 5002번이다`() {
        every { connectionService.makeConnection(any(), any()) } throws PartnerAlreadyHavingConnectionException()

        val requestBody = objectMapper.writeValueAsString(MakingConnectionRequest(UUID.randomUUID().toString()))

        mockMvc.perform(
            MockMvcRequestBuilders.post("/connection/code")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody)
                .header(AUTHENTICATION_HEADER_NAME, "pobi")
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("errorCode").value(5002))
    }

    @Test
    fun `내가 이미 다른 사람과 연결되었을 때 발생하는 오류 코드는 5003번이다`() {
        every { connectionService.makeConnection(any(), any()) } throws UserAlreadyHavingConnectionException()

        val requestBody = objectMapper.writeValueAsString(MakingConnectionRequest(UUID.randomUUID().toString()))

        mockMvc.perform(
            MockMvcRequestBuilders.post("/connection/code")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody)
                .header(AUTHENTICATION_HEADER_NAME, "pobi")
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("errorCode").value(5003))
    }

    @Test
    fun `재연결 불가능한 코드를 입력하여 발생하는 오류 코드는 5001번이다`() {
        every { connectionService.makeConnection(any(), any()) } throws CannotReconnectException()

        val requestBody = objectMapper.writeValueAsString(MakingConnectionRequest(UUID.randomUUID().toString()))

        mockMvc.perform(
            MockMvcRequestBuilders.post("/connection/code")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody)
                .header(AUTHENTICATION_HEADER_NAME, "pobi")
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("errorCode").value(5001))
    }

    @Test
    fun `커플의 연결을 해제할 수 있다`() {
        every { connectionService.disconnect(any()) } just runs

        mockMvc.perform(
            MockMvcRequestBuilders.post("/connection/abort")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHENTICATION_HEADER_NAME, "pobi")
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
    }
}
