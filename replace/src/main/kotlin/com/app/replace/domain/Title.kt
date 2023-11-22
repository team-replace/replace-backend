package com.app.replace.domain

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

private const val TITLE_MAX_LENGTH = 30

@Embeddable
class Title(_title: String) {

    @Column(length = 30)
    val title : String

    init {
        require(_title.length <= TITLE_MAX_LENGTH) { "일기장 제목이 ${TITLE_MAX_LENGTH}자를 초과하였습니다."}
        this.title = _title
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Title

        return title == other.title
    }

    override fun hashCode(): Int {
        return title.hashCode()
    }
}
