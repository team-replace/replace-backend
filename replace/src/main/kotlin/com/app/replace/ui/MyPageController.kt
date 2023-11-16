package com.app.replace.ui

import com.app.replace.application.UserInformation
import com.app.replace.application.UserService
import com.app.replace.ui.argumentresolver.Authenticated
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class MyPageController(val userService: UserService) {
    @GetMapping("/my")
    fun loadUserInformationWithPartner(@Authenticated userId: Long): ResponseEntity<UserInformation> {
        return ResponseEntity.ok(userService.loadUserInformationWithPartner(userId))
    }
}
