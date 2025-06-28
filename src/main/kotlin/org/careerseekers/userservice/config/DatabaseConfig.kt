package org.careerseekers.userservice.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.DriverManagerDataSource
import javax.sql.DataSource

@Configuration
class DatabaseConfig {
    @Value("\${database.url}")
    private lateinit var databaseUrl: String

    @Value("\${database.username}")
    private lateinit var databaseUsername: String

    @Value("\${database.password}")
    private lateinit var databasePassword: String

    @Value("\${database.schema}")
    private lateinit var databaseSchema: String

    @Bean
    fun dataSource(): DataSource {
        val dataSource = DriverManagerDataSource()

        dataSource.url = databaseUrl
        dataSource.username = databaseUsername
        dataSource.password = databasePassword
        dataSource.schema = databaseSchema

        return dataSource
    }
}