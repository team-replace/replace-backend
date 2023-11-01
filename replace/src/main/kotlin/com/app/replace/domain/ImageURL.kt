package com.app.replace.domain

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class ImageURL(
    val url: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    companion object {
        fun from(imageURLs: List<String>): List<ImageURL> {
            return imageURLs
                .map { imageURL -> ImageURL(imageURL) }
                .toList()
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ImageURL

        return url == other.url
    }

    override fun hashCode(): Int {
        return url.hashCode()
    }
}
