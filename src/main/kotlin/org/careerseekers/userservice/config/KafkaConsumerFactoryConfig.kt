package org.careerseekers.userservice.config

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.careerseekers.userservice.dto.KafkaMessagesDto
import org.careerseekers.userservice.serializers.PolymorphicKafkaSerializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.ContainerProperties

@EnableKafka
@Configuration
class KafkaConsumerFactoryConfig(private val kafkaProperties: KafkaProperties) {
    @Bean
    fun consumerFactory(): ConsumerFactory<String, KafkaMessagesDto> {
        val configProps = mapOf(
            /**
             *  Kafka cluster connection settings
             *  Connecting to Kafka and serializing keys and values
             */
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaProperties.bootstrapServers,
            ConsumerConfig.GROUP_ID_CONFIG to "users_service_consumers",
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to PolymorphicKafkaSerializer::class.java,

            /**
             *  Auto commit configuration
             */
            ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG to false,
            ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG to 1000,

            /**
             *  Session and heartbeat settings
             */
            ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG to 20000,
            ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG to 1000,

            /**
             *  Fetch settings
             */
            ConsumerConfig.FETCH_MIN_BYTES_CONFIG to 1,
            ConsumerConfig.FETCH_MAX_BYTES_CONFIG to 52428800, // 50MB
            ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG to 1048576, // 1MB

            /**
             * Offset reset policy
             */
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest",

            /**
             *  Max poll records
             */
            ConsumerConfig.MAX_POLL_RECORDS_CONFIG to 500,

            /**
             * Isolation level for transactions
             */
            ConsumerConfig.ISOLATION_LEVEL_CONFIG to "read_committed"
        )


        return DefaultKafkaConsumerFactory(configProps)
    }

    @Bean
    fun kafkaListenerContainerFactory(
        consumerFactory: ConsumerFactory<String, KafkaMessagesDto>
    ): ConcurrentKafkaListenerContainerFactory<String, KafkaMessagesDto> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, KafkaMessagesDto>()

        factory.consumerFactory = consumerFactory
        factory.containerProperties.ackMode = ContainerProperties.AckMode.MANUAL_IMMEDIATE

        return factory
    }
}