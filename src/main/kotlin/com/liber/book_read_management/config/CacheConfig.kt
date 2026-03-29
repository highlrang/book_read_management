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
        val cacheManager = CaffeineCacheManager("recommendations", "naverBookImages")
        cacheManager.setCaffeine(
            // 추천 결과를 1시간 캐시해 반복 외부 호출을 줄인다.
            Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofHours(1))
                .maximumSize(10_000)
        )
        return cacheManager
    }
}
