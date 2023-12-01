package com.app.replace.application.exception

import com.app.replace.common.exception.BadRequestException

class PartnerAlreadyHavingConnectionException(override val message: String? = "이미 다른 사람과 연결된 코드입니다.") : BadRequestException(5002)
