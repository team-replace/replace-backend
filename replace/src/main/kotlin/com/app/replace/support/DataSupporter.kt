package com.app.replace.support

import com.app.replace.application.UserService
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component

@Component
class DataSupporter(
    val userService: UserService
) {
    @PostConstruct
    fun init() {
        userService.createUser(
            "pobi@gmail.com",
            "password123!",
            "pobi"
        )
    }
}
