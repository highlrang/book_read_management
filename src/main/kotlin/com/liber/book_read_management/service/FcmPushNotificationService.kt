package com.liber.book_read_management.service

import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

private val log = KotlinLogging.logger {}

@Service
class FcmPushNotificationService(
    private val webClientBuilder: WebClient.Builder,
    @Value("\${fcm.server-key:}") private val serverKey: String
) : PushNotificationService {

    override fun sendNotification(token: String, title: String, body: String) {
        if (token.isBlank() || serverKey.isBlank()) {
            return
        }

        val payload = mapOf(
            "to" to token,
            "notification" to mapOf(
                "title" to title,
                "body" to body
            )
        )

        try {
            webClientBuilder.build()
                .post()
                .uri("https://fcm.googleapis.com/fcm/send")
                .header("Authorization", "key=$serverKey")
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(String::class.java)
                .block()
        } catch (e: Exception) {
            log.warn(e) { "Failed to send FCM notification" }
        }
    }
}
