package com.app.replace.domain

import org.springframework.data.jpa.repository.JpaRepository

interface ImageURLRepository : JpaRepository<ImageURL, Long> {
    fun findAllByDiaryIsNull(): List<ImageURL>
}
