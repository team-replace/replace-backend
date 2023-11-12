package com.app.replace.domain

import org.springframework.data.jpa.repository.JpaRepository

interface DiaryRepository : JpaRepository<Diary, Long> {
    fun findByUserIdOrderByCreatedAtDesc(userId: Long): List<Diary>
}
