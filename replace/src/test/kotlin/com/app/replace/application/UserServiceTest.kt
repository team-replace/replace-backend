package com.app.replace.application

import com.app.replace.domain.UserRepository
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.data.repository.findByIdOrNull

@DataJpaTest
@Import(UserService::class)
class UserServiceTest(
    @Autowired val userService: UserService,
    @Autowired val userRepository: UserRepository
) {
    @Test
    @DisplayName("회원을 저장하면 고유의 식별자와, 고유의 연결용 코드를 할당한다.")
    fun createUser() {
        val userId = userService.createUser(
            "test@gmail.com",
            "password123!",
            "test"
        )
        val user = userRepository.findByIdOrNull(userId) ?: fail("회원이 저장되지 않았음")
        user.connectionCode shouldNotBe null
        user.id shouldNotBe null
    }
}
