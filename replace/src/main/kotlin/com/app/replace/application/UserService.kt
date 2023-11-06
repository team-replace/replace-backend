package com.app.replace.application

import com.app.replace.domain.User
import com.app.replace.domain.UserRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UserService(
    val userRepository: UserRepository
) {
    fun createUser(email: String, password: String): Long {
        val connectionCode = UUID.randomUUID().toString()
        return userRepository.save(User(email, password, connectionCode)).id
    }
}
