package com.app.replace.domain

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.throwable.shouldHaveMessage
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class TitleTest {
    @Test
    @DisplayName("제목의 이름은 30자를 초과할 수 없다.")
    fun invalidLengthTitle() {
        shouldThrow<IllegalArgumentException> { Title("글".repeat(31)) }
            .shouldHaveMessage("일기장 제목이 30자를 초과하였습니다.")

        shouldNotThrowAny { Title("글".repeat(30)) }
    }
}
