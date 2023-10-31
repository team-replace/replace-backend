package com.app.replace.application

import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class ImageService(val imageUploadManager: ImageUploadManager) {
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
}

data class ImageUploadingRequest(
    val multipartFile: MultipartFile,
    val imageName: String,
    val imageCategory: ImageCategory
)
