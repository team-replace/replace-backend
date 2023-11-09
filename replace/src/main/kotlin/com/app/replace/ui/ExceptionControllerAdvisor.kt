package com.app.replace.ui

import com.app.replace.common.exception.BadRequestException
import com.app.replace.ui.exception.UnAuthenticatedException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ExceptionControllerAdvisor {

    @ExceptionHandler(UnAuthenticatedException::class)
    fun unAuthenticated(
        unAuthenticatedException: UnAuthenticatedException
    ): ResponseEntity<CommonExceptionFormat> {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(CommonExceptionFormat(0, unAuthenticatedException.message ?: "인증되지 않은 요청으로 접근할 수 없습니다."))
    }

    @ExceptionHandler(BadRequestException::class)
    fun badRequest(
        badRequestException: BadRequestException
    ) : ResponseEntity<CommonExceptionFormat> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(CommonExceptionFormat(badRequestException.code, badRequestException.message!!))
    }
}

data class CommonExceptionFormat(val errorCode: Int, val errorBody: String)
