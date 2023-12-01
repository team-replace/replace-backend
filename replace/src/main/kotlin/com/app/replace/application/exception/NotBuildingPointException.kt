package com.app.replace.application.exception

import com.app.replace.common.exception.BadRequestException

class NotBuildingPointException(override val message: String? = "건물명이 없는 좌표입니다.") : BadRequestException(8000)
