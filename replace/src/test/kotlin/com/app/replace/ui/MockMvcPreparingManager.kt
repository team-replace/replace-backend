package com.app.replace.ui

import com.app.replace.application.AuthenticationInterceptor
import com.app.replace.domain.UserRepository
import com.app.replace.ui.argumentresolver.AuthenticationArgumentResolver
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder
import org.springframework.web.filter.CharacterEncodingFilter
import java.nio.charset.StandardCharsets

abstract class MockMvcPreparingManager(
    val controller: Any,
) {
    @Autowired lateinit var authenticationInterceptor: AuthenticationInterceptor
    @Autowired lateinit var authenticationArgumentResolver: AuthenticationArgumentResolver
    @Autowired lateinit var exceptionControllerAdvisor: ExceptionControllerAdvisor
    @Autowired lateinit var objectMapper: ObjectMapper

    @MockkBean
    lateinit var userRepository: UserRepository

    lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .addFilter<StandaloneMockMvcBuilder?>(CharacterEncodingFilter(StandardCharsets.UTF_8.name(), true))
            .addInterceptors(authenticationInterceptor)
            .setCustomArgumentResolvers(authenticationArgumentResolver)
            .setControllerAdvice(exceptionControllerAdvisor)
            .build()

        every { userRepository.findIdByNickname(any()) } returns 1L
    }
}
