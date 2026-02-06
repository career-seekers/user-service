package org.careerseekers.userservice.services.kafka.consumers

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.support.Acknowledgment

fun interface CustomKafkaConsumer<T, K> {
    fun receiveMessage(consumerRecord: ConsumerRecord<T, K>, acknowledgment: Acknowledgment): Any
}