package com.app.replace.infrastructure

import com.app.replace.application.ImageCategory
import com.app.replace.application.ImageUploadManager
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest

private const val BUCKET_NAME = "replace-s3"
private const val BUCKET_URL_PREFIX = "https://replace-s3.s3.ap-northeast-2.amazonaws.com"

@Component
class S3ImageUploadManager(val s3Client: S3Client) : ImageUploadManager {

    override fun uploadImage(multipartFile: MultipartFile, imageName : String, imageCategory : ImageCategory): String {
        val bucketKey = "${imageCategory.path}/${imageName}"

        val request = PutObjectRequest.builder()
            .bucket(BUCKET_NAME)
            .key(bucketKey)
            .contentType(multipartFile.contentType)
            .build()
        s3Client.putObject(request, RequestBody.fromBytes(multipartFile.bytes))

        return getImageUrl(bucketKey)
    }

    private fun getImageUrl(bucketKey: String): String {
        val path = s3Client.utilities()
            .getUrl { r -> r.bucket(BUCKET_NAME).key(bucketKey) }
            .path
        requireNotNull(path) { "저장된 이미지의 URL이 존재하지 않습니다."}
        return BUCKET_URL_PREFIX + path
    }
}
