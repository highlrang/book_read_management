package com.liber.book_read_management.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ClassPathResource
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import java.security.SecureRandom
import java.util.*
import kotlin.math.max

@Service
class EmailServiceImpl(
    @Value("\${auth.email-verification.link-base-url:}") private val emailVerificationLinkBaseUrl: String,
    private val javaMailSender: JavaMailSender,
    private val emailVerificationService: EmailVerificationService
) : EmailService {

    private val secureRandom = SecureRandom()
    private val emailHeaderColor = "#FFD700"
    private val emailAccentColor = "#FFD700"
    private val emailInlineLogoContentId = "email-header-icon"
    private val emailInlineWordmarkContentId = "email-header-wordmark"

    override fun sendVerificationEmail(email: String) {
        val verificationToken = generateVerificationToken()
        emailVerificationService.issueToken(email, verificationToken)
        val verificationUrl = buildVerificationUrl(verificationToken)

        val message = javaMailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, true)

        helper.setTo(email)
        helper.setSubject("[한장해] 이메일 인증을 완료해주세요")
        val ttlMinutes = max(1, emailVerificationService.getTtlSeconds() / 60)
        val htmlBody = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="utf-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>이메일 인증</title>
            </head>
            <body style="margin: 0; padding: 0; background-color: #f4f7f9; font-family: 'Apple SD Gothic Neo', 'Malgun Gothic', sans-serif;">
                <table align="center" border="0" cellpadding="0" cellspacing="0" width="100%" style="max-width: 600px; margin: 40px auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 10px rgba(0,0,0,0.05);">
                    <tr>
                        <td style="padding: 32px 20px; text-align: center; background-color: #ffffff; border: 2px solid ${emailHeaderColor}; border-radius: 12px;">
                            <table align="center" border="0" cellpadding="0" cellspacing="0" style="margin: 0 auto;">
                                <tr>
                                    <td style="padding: 14px 18px; vertical-align: middle;">
                                        <table align="center" border="0" cellpadding="0" cellspacing="0">
                                            <tr>
                                                <td style="padding: 0 12px 0 0; vertical-align: middle;">
                                                    <img src="cid:${emailInlineLogoContentId}" alt="한장해 아이콘" width="52" height="52" style="display: block; width: 52px; height: 52px;">
                                                </td>
                                                <td style="vertical-align: middle;">
                                                    <img src="cid:${emailInlineWordmarkContentId}" alt="한장해" style="display: block; max-width: 180px; width: 100%; height: auto;">
                                                </td>
                                            </tr>
                                        </table>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                    
                    <tr>
                        <td style="padding: 40px 30px; text-align: center;">
                            <h2 style="margin: 0 0 20px; color: #333333; font-size: 20px;">이메일 인증을 완료해주세요.</h2>
                            <p style="margin: 0 0 30px; color: #666666; font-size: 16px; line-height: 1.6;">
                                한장해 회원가입을 계속하려면 아래 버튼을 눌러 앱에서 인증을 완료해 주세요.<br>
                                인증 링크는 발급 시점부터 <strong>${ttlMinutes}분 동안</strong>만 유효합니다.
                            </p>
                            
                            <a href="${verificationUrl}" target="_blank" style="display: inline-block; padding: 16px 36px; background-color: ${emailAccentColor}; color: #ffffff; text-decoration: none; font-size: 16px; font-weight: bold; border-radius: 4px; box-shadow: 0 2px 5px rgba(0,0,0,0.1);">
                                앱에서 이메일 인증하기
                            </a>

                            <p style="margin: 24px 0 0; color: #666666; font-size: 14px; line-height: 1.6;">
                                앱이 설치된 모바일 기기에서 버튼을 눌러야 인증이 정상적으로 진행됩니다.<br>
                                버튼이 동작하지 않으면 아래 링크를 복사해 모바일 브라우저 주소창에 붙여 넣어 주세요.
                            </p>

                            <p style="margin: 16px 0 0; padding: 14px 16px; background-color: #f4f7f9; border-radius: 6px; color: #2c3e50; font-size: 13px; line-height: 1.6; word-break: break-all;">
                                ${verificationUrl}
                            </p>

                            <p style="margin: 30px 0 0; color: #999999; font-size: 14px; line-height: 1.6;">
                                만약 본인이 가입한 것이 아니라면 이 메일을 무시하셔도 됩니다.
                            </p>
                        </td>
                    </tr>
                    
                    <tr>
                        <td style="padding: 20px; text-align: center; background-color: #f9fafb; color: #aaaaaa; font-size: 12px;">
                            © 2026 ppiyakworld. All rights reserved.<br>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
        """.trimIndent()
        helper.setText(htmlBody, true)
        helper.addInline(emailInlineLogoContentId, ClassPathResource("icons/icon.png"))
        helper.addInline(emailInlineWordmarkContentId, ClassPathResource("icons/logo.png"))

        javaMailSender.send(message)
    }

    private fun generateVerificationToken(): String {
        val bytes = ByteArray(32)
        secureRandom.nextBytes(bytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }

    private fun buildVerificationUrl(token: String): String {
        val baseUrl = emailVerificationLinkBaseUrl.ifBlank { "readingorganizer://verify" }
        val separator = if (baseUrl.contains("?")) "&" else "?"
        return "${baseUrl}${separator}token=${token}"
    }
}
