package com.app.replace.application

import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import

@DataJpaTest
@Import(ConnectionService::class, UserService::class)
class ConnectionServiceTest(
    @Autowired private val connectionService: ConnectionService
) {
    @Test
    fun `주어진 식별자를 이용하면 회원의 고유한 코드를 얻어올 수 있다`(
        @Autowired userService: UserService
    ) {
        val userId = `creates a User and returns id`(userService)
        connectionService.loadConnection(userId) shouldNotBe null
    }

    private fun `creates a User and returns id`(userService: UserService) =
        userService.createUser("test@gmail.com", "Password123!", "Hongo")
}
