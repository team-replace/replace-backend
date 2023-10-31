package com.app.replace.application

import org.springframework.web.multipart.MultipartFile

fun interface ImageUploadManager {
    fun uploadImage(multipartFile: MultipartFile, imageName : String, imageCategory : ImageCategory): String
}
