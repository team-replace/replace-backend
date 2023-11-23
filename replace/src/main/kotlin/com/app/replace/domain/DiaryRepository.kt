package com.app.replace.domain

import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface DiaryRepository : JpaRepository<Diary, Long> {
    fun findByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(
        userId: Long,
        from: LocalDateTime,
        to: LocalDateTime
    ): List<Diary>
}
