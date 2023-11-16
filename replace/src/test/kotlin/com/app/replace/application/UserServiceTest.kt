package com.app.replace.application

import com.app.replace.domain.UserRepository
import com.ninjasquad.springmockk.MockkBean
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import org.assertj.core.api.Assertions
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
    @MockkBean
    private lateinit var connectionService: ConnectionService

    @Test
    fun `회원을 저장하면 고유의 식별자와, 고유의 연결용 코드를 할당한다`() {
        val userId = `creates a user and returns id`("test@gmail.com", "test")
        val user = userRepository.findByIdOrNull(userId) ?: fail("회원이 저장되지 않았음")
        user.connectionCode shouldNotBe null
        user.id shouldNotBe null
    }

    @Test
    fun `회원과 연결된 파트너가 있다면 유저와 파트너의 정보를 가져온다`() {
        val userId = `creates a user and returns id`("test@gmail.com", "test")
        val userId2 = `creates a user and returns id`("test2@gmail.com", "test2")
        every { connectionService.findPartnerIdByUserId(userId) } returns userId2

        val userInformation = userService.loadUserInformationWithPartner(userId)
        Assertions.assertThat(userInformation).isInstanceOf(UserInformationWithPartner::class.java)
        (userInformation as UserInformationWithPartner).user.nickname shouldBe "test"
        userInformation.partner.nickname shouldBe "test2"
    }

    @Test
    fun `회원과 연결된 파트너가 없다면 유저의 정보만 가져온다`() {
        val userId = `creates a user and returns id`("test@gmail.com", "test")
        every { connectionService.findPartnerIdByUserId(userId) } returns null

        val userInformation = userService.loadUserInformationWithPartner(userId)
        Assertions.assertThat(userInformation).isInstanceOf(AloneUserInformation::class.java)
        (userInformation as AloneUserInformation).user.nickname shouldBe "test"
    }

    private fun `creates a user and returns id`(email: String, nickname: String) = userService.createUser(
        email, "password123!", nickname
    )
}
