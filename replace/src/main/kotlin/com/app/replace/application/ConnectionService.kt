package com.app.replace.application

import com.app.replace.domain.UserRepository
import org.springframework.stereotype.Service

@Service
class ConnectionService(
    private val userRepository: UserRepository
) {
    fun loadConnection(userId: Long) : String {
        return userRepository.findConnectionCodeById(userId)
            ?: throw IllegalArgumentException("식별자에 해당하는 회원이 존재하지 않거나, 회원은 존재하나 고유 코드가 존재하지 않습니다.")
    }
}
