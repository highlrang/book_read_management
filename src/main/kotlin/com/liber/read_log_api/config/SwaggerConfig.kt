package com.liber.read_log_api.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig() {
    @Bean
    fun userApi(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("user-api")
            .pathsToMatch("/api/v1/**")
            .pathsToExclude("/api/v1/admin/**")
            .build()
    }

    @Bean
    fun openAPI(): OpenAPI {

        // API 메타 정보
        val info: Info = Info()
            .title("Book Record API")
            .version("1.0.0")
            .termsOfService("")
            .contact(Contact().name("JHHW").url(""))
        val apiKeyScheme: SecurityScheme = SecurityScheme()
            .name("API_Key")
            .type(SecurityScheme.Type.APIKEY)
            .`in`(SecurityScheme.In.HEADER)
            .description("API Key")
        val bearerScheme: SecurityScheme = SecurityScheme()
            .name("Authorization")
            .type(SecurityScheme.Type.HTTP)
            .`in`(SecurityScheme.In.HEADER)
            .scheme("bearer")
            .bearerFormat("JWT")
            .description("JWT Access Token (Bearer 없이 입력)")

        // 서버 URL: 리버스 프록시/게이트웨이를 타는 경우 "/"가 안전
        val server: Server = Server().url("/")
        return OpenAPI()
            .info(info)
            .components(
                Components()
                    .addSecuritySchemes("API_Key", apiKeyScheme)
                    .addSecuritySchemes("bearerAuth", bearerScheme)
            )
            .addSecurityItem(SecurityRequirement().addList("API_Key"))
            .addSecurityItem(SecurityRequirement().addList("bearerAuth"))
            .addServersItem(server)
    }
}