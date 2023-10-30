package com.app.replace.domain

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.throwable.shouldHaveMessage
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class ContentTest {
    @Test
    @DisplayName("일기장 내용의 이름은 30자를 초과할 수 없다.")
    fun invalidLengthContent() {
        shouldThrow<IllegalArgumentException> { Content("글".repeat(3001)) }
            .shouldHaveMessage("일기장의 내용은 3000자를 넘을 수 없습니다.")

        shouldNotThrowAny { Content("글".repeat(3000)) }
    }
}
