package com.app.replace.domain

import jakarta.persistence.ElementCollection
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class Diary(
    @Embedded val title: Title,
    @Embedded val content: Content,
    @Embedded val place: Place,
    @ElementCollection val imageURLs: List<String>,
    private val shareScope: ShareScope
 ) : TemporalRecord() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?

    init {
        this.id = null
    }
}
