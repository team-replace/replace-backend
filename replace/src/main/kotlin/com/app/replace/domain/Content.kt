package com.app.replace.domain

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

private const val CONTENT_MAX_LENGTH = 3000

@Embeddable
class Content(_content: String) {

    @Column
    val content: String

    init {
        require(_content.length <= CONTENT_MAX_LENGTH) { "일기장의 내용은 ${CONTENT_MAX_LENGTH}자를 넘을 수 없습니다."}
        this.content = _content
    }
}
