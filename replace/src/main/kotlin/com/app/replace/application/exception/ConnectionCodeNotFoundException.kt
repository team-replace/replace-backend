package com.app.replace.application.exception

import com.app.replace.common.exception.BadRequestException

class ConnectionCodeNotFoundException(override val message: String? = "존재하지 않는 코드입니다.") : BadRequestException(5000)
