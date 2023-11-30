package com.app.replace.application.exception

import com.app.replace.common.exception.BadRequestException

class IllegalCoordinateException(override val message: String? = "장소를 특정할 수 없는 좌표입니다.") : BadRequestException(8001)
