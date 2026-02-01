package com.liber.book_read_management.service

import com.liber.book_read_management.repository.redis.AuthRedisStore
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import java.util.*

@Service
class EmailServiceImpl(
    @Value("\${myDomain}") private val myDomain: String,
    private val javaMailSender: JavaMailSender,
    private val authRedisStore: AuthRedisStore
) : EmailService {

    override fun sendVerificationEmail(email: String) {
        val verificationCode = String.format("%06d", Random().nextInt(1000000))

        authRedisStore.setValue(email, verificationCode)

        val verificationUrl = "${myDomain}/auth/verify-email?email=${email}&code=${verificationCode}"

        val message = javaMailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, true)

        helper.setTo(email)
        helper.setSubject("[한장해] 회원가입 인증 코드")
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
                        <td style="padding: 40px 20px; text-align: center; background-color: #4A90E2;">
                            <h1 style="margin: 0; color: #ffffff; font-size: 24px; font-weight: 700;">서비스 이름</h1>
                        </td>
                    </tr>
                    
                    <tr>
                        <td style="padding: 40px 30px; text-align: center;">
                            <h2 style="margin: 0 0 20px; color: #333333; font-size: 20px;">반갑습니다! 이메일 인증을 완료해주세요.</h2>
                            <p style="margin: 0 0 30px; color: #666666; font-size: 16px; line-height: 1.6;">
                                가입을 계속하시려면 아래 버튼을 클릭하여 이메일 주소를 인증해 주세요.<br>
                                이 링크는 <strong>10분 동안</strong>만 유효합니다.
                            </p>
                            
                            <a href="${verificationUrl}" target="_blank" style="display: inline-block; padding: 16px 36px; background-color: #4A90E2; color: #ffffff; text-decoration: none; font-size: 16px; font-weight: bold; border-radius: 4px; box-shadow: 0 2px 5px rgba(0,0,0,0.1);">
                                이메일 인증하기
                            </a>
                            
                            <p style="margin: 30px 0 0; color: #999999; font-size: 14px;">
                                만약 본인이 가입한 것이 아니라면 이 메일을 무시하셔도 됩니다.
                            </p>
                        </td>
                    </tr>
                    
                    <tr>
                        <td style="padding: 20px; text-align: center; background-color: #f9fafb; color: #aaaaaa; font-size: 12px;">
                            © 2026 서비스이름. All rights reserved.<br>
                            서울특별시 어딘가 구 무엇동 123-45
                        </td>
                    </tr>
                </table>
            </body>
            </html>
        """.trimIndent()
        helper.setText(htmlBody, true)

        javaMailSender.send(message)
    }
}