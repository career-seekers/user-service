package org.careerseekers.userservice.config.kafka.producers

import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.careerseekers.userservice.dto.EmailSendingTaskDto
import org.careerseekers.userservice.serializers.PolymorphicKafkaSerializer
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory

@Configuration
@ConfigurationProperties(prefix = "spring.kafka")
class EmailSendingProducerConfig {

    lateinit var bootstrapServers: String

    @Bean
    fun producerFactory(): ProducerFactory<String, EmailSendingTaskDto> {
        val configProps = mapOf(

            /**
             * Kafka cluster connection settings
             * Connecting to Kafka and serializing keys and values
             */
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to PolymorphicKafkaSerializer::class.java,

            /**
             * Retries settings
             * Repeated attempts to send messages with temporary errors
             */
            ProducerConfig.RETRIES_CONFIG to Int.MAX_VALUE,
            ProducerConfig.RETRY_BACKOFF_MS_CONFIG to 500,

            /**
             * Reliability and ordering settings
             * Guarantees of delivery and order of messages
             */
            ProducerConfig.ACKS_CONFIG to "all",
            ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG to true,
            ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION to 5,

            /**
             * Performance tuning
             * Batching and delay settings to increase throughput
             */
            ProducerConfig.LINGER_MS_CONFIG to 100,
            ProducerConfig.BATCH_SIZE_CONFIG to 32 * 1024,

            /**
             * Timeout settings
             * Timeouts for request processing and message delivery
             */
            ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG to 30_000,
            ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG to 120_000
        )

        return DefaultKafkaProducerFactory(configProps)
    }

    @Bean
    fun emailSendingKafkaProducer(): KafkaTemplate<String, EmailSendingTaskDto> {
        return KafkaTemplate(producerFactory())
    }
}