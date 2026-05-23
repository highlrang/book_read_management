package com.liber.book_read_management

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@EnableFeignClients
@EnableJpaAuditing
@ConfigurationPropertiesScan
@SpringBootApplication
class ReadLogApiApplication
fun main(args: Array<String>) {
    runApplication<ReadLogApiApplication>(*args)
}
