package com.app.replace.application.exception

import com.app.replace.common.exception.BadRequestException

class ConnectingWithItSelfException(override val message: String? = "자기 자신과 연결할 수 없습니다.") : BadRequestException(5004)
