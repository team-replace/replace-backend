package com.app.replace.ui.request

import org.springframework.web.multipart.MultipartFile

data class ImageUploadingRequest(val images: List<MultipartFile>)
