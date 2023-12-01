package com.app.replace.application.exception

import com.app.replace.common.exception.BadRequestException

class UserAlreadyHavingConnectionException(override val message: String? = "귀하의 계정이 이미 다른 사람과 연결되어 있습니다.") : BadRequestException(5003)
