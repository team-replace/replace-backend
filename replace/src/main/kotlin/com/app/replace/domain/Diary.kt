package com.app.replace.domain

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class Diary(
    @Embedded var title: Title,
    @Embedded var content: Content,
    @Embedded var place: Place,
    imageURLs: List<ImageURL>,
    var userId: Long?,
    @Enumerated(EnumType.STRING)
    var shareScope: ShareScope,
    override var createdAt: LocalDateTime = LocalDateTime.now()
) : TemporalRecord(createdAt) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?

    @OneToMany(cascade = [CascadeType.PERSIST])
    @JoinColumn(name = "diary_id")
    var imageURLs: MutableList<ImageURL>

    init {
        this.id = null
        this.imageURLs = imageURLs.toMutableList()
    }

    fun update(diary: Diary) {
        this.title = diary.title
        this.content = diary.content
        this.place = diary.place
        this.shareScope = diary.shareScope
        updateImageUrls(diary)
    }

    private fun updateImageUrls(diary: Diary) {
        val additionalImageURLs = findAdditionalURLs(diary)
        val removalImageURLs = findRemovalURLs(diary)
        this.imageURLs.removeAll(removalImageURLs)
        this.imageURLs.addAll(additionalImageURLs)
    }

    private fun findRemovalURLs(diary: Diary): MutableList<ImageURL> {
        val removalImageURLs = this.imageURLs.toMutableList()
        for (imageURL in diary.imageURLs) {
            if (imageURLs.contains(imageURL)) {
                removalImageURLs.remove(imageURL)
            }
        }
        return removalImageURLs
    }

    private fun findAdditionalURLs(diary: Diary): MutableList<ImageURL> {
        val additionalImageURLs = diary.imageURLs.toMutableList()
        for (imageURL in diary.imageURLs) {
            if (imageURLs.contains(imageURL)) {
                additionalImageURLs.remove(imageURL)
            }
        }
        return additionalImageURLs
    }
}
