package com.app.replace.domain

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

private const val TITLE_MAX_LENGTH = 30

@Embeddable
class Title(_title: String) {

    @Column
    val title : String

    init {
        require(_title.length <= TITLE_MAX_LENGTH) { "일기장 제목이 ${TITLE_MAX_LENGTH}자를 초과하였습니다."}
        this.title = _title
    }
}
