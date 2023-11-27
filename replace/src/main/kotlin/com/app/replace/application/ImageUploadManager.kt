package com.app.replace.application

import org.springframework.web.multipart.MultipartFile

interface ImageUploadManager {
    fun uploadImage(multipartFile: MultipartFile, imageName : String, imageCategory : ImageCategory): String

    fun removeAll(urls: List<String>)
}
