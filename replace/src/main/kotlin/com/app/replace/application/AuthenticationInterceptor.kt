package com.app.replace.application

import com.app.replace.domain.UserRepository
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

const val AUTHENTICATION_HEADER_NAME = "temporary"

const val AUTH_USER_KEY = "UserId"

@Component
class AuthenticationInterceptor(
    private val userRepository: UserRepository
) : HandlerInterceptor {
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val nickname = request.getHeader(AUTHENTICATION_HEADER_NAME) ?: return true
        val userId = userRepository.findIdByNickname(nickname) ?: return true

        request.setAttribute(AUTH_USER_KEY, userId)
        return true
    }
}
