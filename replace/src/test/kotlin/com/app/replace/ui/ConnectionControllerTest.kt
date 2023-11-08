package com.app.replace.ui

import com.app.replace.application.AUTHENTICATION_HEADER_NAME
import com.app.replace.application.ConnectionService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.util.UUID

@WebMvcTest(controllers = [ConnectionController::class])
@AutoConfigureMockMvc
class ConnectionControllerTest(
    @Autowired private val connectionController: ConnectionController
) : MockMvcPreparingManager(connectionController) {

    @MockkBean lateinit var connectionService: ConnectionService

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
}
