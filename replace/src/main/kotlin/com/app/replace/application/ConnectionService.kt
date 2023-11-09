package com.app.replace.application

import com.app.replace.common.exception.BadRequestException
import com.app.replace.domai.ConnectionRepository
import com.app.replace.domain.Connection
import com.app.replace.domain.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Objects

@Service
@Transactional
class ConnectionService(
    private val userRepository: UserRepository,
    private val connectionRepository: ConnectionRepository
) {
    fun loadConnection(userId: Long) : String {
        return userRepository.findConnectionCodeById(userId)
            ?: throw IllegalArgumentException("식별자에 해당하는 회원이 존재하지 않거나, 회원은 존재하나 고유 코드가 존재하지 않습니다.")
    }

    fun makeConnection(userId: Long, partnerCode: String) {
        val partnerId = userRepository.findIdByConnectionCode(partnerCode)
            ?: throw ConnectionCodeNotFoundException()

        `is connecting with myself`(userId, partnerId)
        `am I already connected with another`(userId)
        `is partner already connected with another`(partnerId)
        // TODO: 삭제된 후 다시 연결할 수 없는 제약사항을 추가해야 한다

        connectionRepository.save(
            Connection(arrayOf(userId, partnerId).min(), arrayOf(userId, partnerId).max())
        )
    }

    private fun `is connecting with myself`(userId: Long, partnerId: Long) {
        if (Objects.equals(userId, partnerId)) {
            throw ConnectingWithItSelfException()
        }
    }

    private fun `is partner already connected with another`(partnerId: Long) {
        if (connectionRepository.existsConnectionHavingId(partnerId)) {
            throw PartnerAlreadyHavingConnectionException()
        }
    }

    private fun `am I already connected with another`(userId: Long) {
        if (connectionRepository.existsConnectionHavingId(userId)) {
            throw UserAlreadyHavingConnectionException()
        }
    }
}

class ConnectionCodeNotFoundException(override val message: String? = "존재하지 않는 코드입니다.") : BadRequestException(5000)
class CannotReconnectException(override val message: String? = "다시 연결할 수 없는 코드입니다.") : BadRequestException(5001)
class PartnerAlreadyHavingConnectionException(override val message: String? = "이미 다른 사람과 연결된 코드입니다.") : BadRequestException(5002)
class UserAlreadyHavingConnectionException(override val message: String? = "귀하의 계정이 이미 다른 사람과 연결되어 있습니다.") : BadRequestException(5003)
class ConnectingWithItSelfException(override val message: String? = "자기 자신과 연결할 수 없습니다.") : BadRequestException(5004)
