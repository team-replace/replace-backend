package com.app.replace.application.exception

import com.app.replace.common.exception.BadRequestException

class InvalidDateException(override val message: String? = "선택할 수 없는 날짜입니다.") : BadRequestException(7000)
