package com.app.replace.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client

@Configuration
class AWSConfig(val environment: Environment) {

    @Bean
    fun registerS3Client(): S3Client {
        val credentials = AwsBasicCredentials.create(
            environment.getProperty("aws_access_key_id"),
            environment.getProperty("aws_secret_access_key")
        )

        return S3Client.builder()
            .region(Region.AP_NORTHEAST_2)
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .build()
    }
}
