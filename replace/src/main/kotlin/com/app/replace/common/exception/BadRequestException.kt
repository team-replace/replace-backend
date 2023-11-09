package com.app.replace.common.exception

open class BadRequestException(
    val code: Int
) : RuntimeException()
