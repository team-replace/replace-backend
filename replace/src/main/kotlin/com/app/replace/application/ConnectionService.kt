package com.app.replace.application

import com.app.replace.application.exception.*
import com.app.replace.domai.ConnectionRepository
import com.app.replace.domain.Connection
import com.app.replace.domain.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class ConnectionService(
    private val userRepository: UserRepository,
    private val connectionRepository: ConnectionRepository
) {
    @Transactional(readOnly = true)
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
        `have they been broken once`(userId, partnerId)

        connectionRepository.save(
            Connection(arrayOf(userId, partnerId).min(), arrayOf(userId, partnerId).max())
        )
    }

    fun disconnect(userId: Long) {
        connectionRepository.deleteByUserId(userId)
    }

    @Transactional(readOnly = true)
    fun findPartnerIdByUserId(userId: Long): Long? {
        val connection = connectionRepository.findHavingId(userId) ?: return null
        return if (Objects.equals(connection.hostId, userId)) {
            connection.partnerId
        } else {
            connection.hostId
        }
    }

    private fun `is connecting with myself`(userId: Long, partnerId: Long) {
        if (Objects.equals(userId, partnerId)) {
            throw ConnectingWithItSelfException()
        }
    }

    private fun `is partner already connected with another`(partnerId: Long) {
        if (connectionRepository.existsConnectionHavingUserId(partnerId)) {
            throw PartnerAlreadyHavingConnectionException()
        }
    }

    private fun `am I already connected with another`(userId: Long) {
        if (connectionRepository.existsConnectionHavingUserId(userId)) {
            throw UserAlreadyHavingConnectionException()
        }
    }

    private fun `have they been broken once`(userId: Long, partnerId: Long) {
        if (connectionRepository.existsDeletedConnectionHavingHostIdAndPartnerId(userId, partnerId)) {
            throw CannotReconnectException()
        }
    }
}

