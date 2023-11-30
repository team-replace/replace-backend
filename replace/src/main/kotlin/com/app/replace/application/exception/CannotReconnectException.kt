package com.app.replace.application.exception

import com.app.replace.common.exception.BadRequestException

class CannotReconnectException(override val message: String? = "다시 연결할 수 없는 코드입니다.") : BadRequestException(5001)
