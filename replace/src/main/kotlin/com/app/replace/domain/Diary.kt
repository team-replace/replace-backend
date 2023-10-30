package com.app.replace.domain

import jakarta.persistence.ElementCollection
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class Diary(
    @Embedded private val title: Title,
    @Embedded private val content: Content,
    @ElementCollection private val imageURLS: List<String>,
    private val shareScope: ShareScope
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?

    init {
        this.id = null
    }
}
