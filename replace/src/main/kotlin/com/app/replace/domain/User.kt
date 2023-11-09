package com.app.replace.domain

import jakarta.persistence.*

const val CODE_MAX_LENGTH = 10

@Entity
@Table(name = "users")
class User(
    @Column(unique = true, nullable = false)
    val email: String,

    @Column(unique = true, nullable = false)
    val nickname: String,

    @Column(nullable = false)
    val password: String,

    @Column(unique = true, nullable = false)
    val connectionCode: String,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L
) : TemporalRecord() {
    init {
        if (CODE_MAX_LENGTH < connectionCode.length) {
            throw IllegalArgumentException("코드의 길이가 ${CODE_MAX_LENGTH}자를 초과할 수 없습니다.")
        }
    }
}
