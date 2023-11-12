package com.app.replace.application

import com.app.replace.domai.ConnectionRepository
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThatThrownBy
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
        val userId = `creates a User and returns id`(userService, "test@gmail.com", "Hongo")
        connectionService.loadConnection(userId) shouldNotBe null
    }

    @Test
    fun `나에게 고유하게 할당된 코드를 가져올 수 있다`(
        @Autowired userService: UserService
    ) {
        val myId = `creates a User and returns id`(userService, "test@gmail.com", "Hongo")
        val partnerId = `creates a User and returns id`(userService, "test2@gmail.com", "Hongo2")
        val connectionCode = connectionService.loadConnection(partnerId)

        connectionService.makeConnection(myId, connectionCode)
    }

    @Test
    fun `존재하지 않는 코드를 입력하여 발생하는 오류 코드는 5000번이다`(
        @Autowired userService: UserService
    ) {
        val myId = `creates a User and returns id`(userService, "test@gmail.com", "Hongo")
        assertThatThrownBy { connectionService.makeConnection(myId, "존재하지 않는 코드") }
            .isInstanceOf(ConnectionCodeNotFoundException::class.java)
            .hasMessage("존재하지 않는 코드입니다.")
            .extracting("code").isEqualTo(5000)
    }


    @Test
    fun `이미 다른 사람과 연결된 코드를 입력하려 했을 때 발생하는 오류 코드는 5002번이다`(
        @Autowired userService: UserService,
        @Autowired entityManager: EntityManager
    ) {
        val myId = `creates a User and returns id`(userService, "test@gmail.com", "Hongo")
        val partnerId = `creates a User and returns id`(userService, "test2@gmail.com", "Hongo2")
        `make someone connect with another`(userService, partnerId)
        entityManager.flush()

        val partnerConnectionCode = connectionService.loadConnection(partnerId)
        assertThatThrownBy { connectionService.makeConnection(myId, partnerConnectionCode) }
            .isInstanceOf(PartnerAlreadyHavingConnectionException::class.java)
            .hasMessage("이미 다른 사람과 연결된 코드입니다.")
            .extracting("code").isEqualTo(5002)
    }

    @Test
    fun `내가 이미 다른 사람과 연결되었을 때 발생하는 오류 코드는 5003번이다`(
        @Autowired userService: UserService,
        @Autowired entityManager: EntityManager
    ) {
        val `my id` = `creates a User and returns id`(userService, "test@gmail.com", "Hongo")
        val `partner id` = `creates a User and returns id`(userService, "test2@gmail.com", "Hongo2")
        `make someone connect with another`(userService, `my id`)
        entityManager.flush()

        val partnerConnectionCode = connectionService.loadConnection(`partner id`)
        assertThatThrownBy { connectionService.makeConnection(`my id`, partnerConnectionCode) }
            .isInstanceOf(UserAlreadyHavingConnectionException::class.java)
            .hasMessage("귀하의 계정이 이미 다른 사람과 연결되어 있습니다.")
            .extracting("code").isEqualTo(5003)
    }

    @Test
    fun `당연하지만 자기 자신과 연결할 수 없다`(
        @Autowired userService: UserService,
        @Autowired entityManager: EntityManager
    ) {
        val userId = `creates a User and returns id`(userService, "test@gmail.com", "Hongo")
        val myCode = connectionService.loadConnection(userId)
        assertThatThrownBy { connectionService.makeConnection(userId, myCode) }
            .isInstanceOf(ConnectingWithItSelfException::class.java)
            .hasMessage("자기 자신과 연결할 수 없습니다.")
            .extracting("code").isEqualTo(5004)
    }

    @Test
    fun `연결된 사람이 존재한다면 연결을 끊을 수 있다`(
        @Autowired userService: UserService,
        @Autowired connectionRepository: ConnectionRepository
    ) {
        val userId = `creates a User and returns id`(userService, "test@gmail.com", "test")
        `make someone connect with another`(userService, userId)
        connectionRepository.existsConnectionHavingUserId(userId) shouldBe true

        connectionService.disconnect(userId)
        connectionRepository.existsConnectionHavingUserId(userId) shouldBe false
    }

    @Test
    fun `재연결 불가능한 코드를 입력하여 발생하는 오류 코드는 5001번이다`(
        @Autowired userService: UserService
    ) {
        val userId = `creates a User and returns id`(userService, "test@gmail.com", "test")
        val userId2 = `creates a User and returns id`(userService, "test2@gmail.com", "test2")
        val connectionCode = connectionService.loadConnection(userId2)
        connectionService.makeConnection(userId, connectionCode)
        connectionService.disconnect(userId)

        assertThatThrownBy { connectionService.makeConnection(userId, connectionCode) }
            .isInstanceOf(CannotReconnectException::class.java)
            .hasMessage("다시 연결할 수 없는 코드입니다.")
            .extracting("code").isEqualTo(5001)
    }

    private fun `creates a User and returns id`(userService: UserService, email: String, nickname: String) =
        userService.createUser(email, "Password123!", nickname)

    private fun `make someone connect with another`(userService: UserService, partnerId: Long) {
        val anotherId = `creates a User and returns id`(userService, "test3@gmail.com", "Hongo3")
        val connectionCode = connectionService.loadConnection(anotherId)
        connectionService.makeConnection(partnerId, connectionCode)
    }
}
