//package com.liber.read_log_api.config
//
//import org.springframework.beans.factory.annotation.Value
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Configuration
//import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
//import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
//import software.amazon.awssdk.regions.Region
//import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient
//
//
//@Configuration
//class AwsConfig {
//    @Value("\${aws.accessKeyId}")
//    private val accessKey: String? = null
//
//    @Value("\${aws.secretAccessKey}")
//    private val secretKey: String? = null
//
//    @Value("\${aws.region}")
//    private val region: String? = null
//    @Bean
//    fun secretsManagerClient(): SecretsManagerClient {
//        val creds = AwsBasicCredentials.create(accessKey, secretKey)
//        return SecretsManagerClient.builder()
//            .credentialsProvider(StaticCredentialsProvider.create(creds))
//            .region(Region.of(region))
//            .build()
//    }
//}