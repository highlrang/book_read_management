package com.liber.book_read_management.config

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JpaConfig(
    val entityManager: EntityManager
) {

    @Bean
    fun jpaQueryFactory() : JPAQueryFactory {
        return JPAQueryFactory(entityManager)
    }
}