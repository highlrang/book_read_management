package com.liber.book_read_management.config

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
@EnableCaching
class CacheConfig {

    @Bean
    fun cacheManager(): CacheManager {
        val cacheManager = CaffeineCacheManager("recommendations")
        cacheManager.setCaffeine(
            // 추천 결과 전체를 1시간 캐시해서, 같은 입력으로 반복 호출될 때 LLM/외부 API 비용을 줄인다.
            Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofHours(1))
                .maximumSize(1_000)
        )
        return cacheManager
    }
}
