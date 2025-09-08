package org.careerseekers.userservice.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.DriverManagerDataSource
import javax.sql.DataSource

@Configuration
@ConfigurationProperties(prefix = "database")
class DatabaseConfig {
    lateinit var url: String
    lateinit var username: String
    lateinit var password: String
    lateinit var schema: String

    @Bean
    fun dataSource(): DataSource {
        val dataSource = DriverManagerDataSource()

        dataSource.url = url
        dataSource.username = username
        dataSource.password = password
        dataSource.schema = schema

        return dataSource
    }
}