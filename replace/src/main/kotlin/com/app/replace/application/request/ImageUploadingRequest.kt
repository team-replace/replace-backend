package com.app.replace.application.request

import com.app.replace.application.ImageCategory
import org.springframework.web.multipart.MultipartFile

data class ImageUploadingRequest(
    val multipartFile: MultipartFile,
    val imageName: String,
    val imageCategory: ImageCategory
)
