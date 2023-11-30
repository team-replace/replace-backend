package com.app.replace.application

import com.app.replace.application.request.ImageUploadingRequest
import com.app.replace.domain.ImageURLRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class ImageService(
    private val imageUploadManager: ImageUploadManager,
    private val imageURLRepository: ImageURLRepository
) {
    fun uploadImage(imageUploadingRequests: List<ImageUploadingRequest>): List<String> {
        return imageUploadingRequests
            .map { request ->
                imageUploadManager.uploadImage(
                    request.multipartFile,
                    request.imageName,
                    request.imageCategory
                )
            }
            .toMutableList()
    }

    @Scheduled(cron = "0 0 0 * * *")
    fun removeUnusedImages() {
        val unusedImages = imageURLRepository.findAllByDiaryIsNull()
        if (unusedImages.isEmpty()) return
        imageUploadManager.removeAll(unusedImages.map { image -> image.url }.toList())
        imageURLRepository.deleteAllInBatch(unusedImages)
    }
}
