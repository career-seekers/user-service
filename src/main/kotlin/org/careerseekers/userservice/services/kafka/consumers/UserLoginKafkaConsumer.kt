package org.careerseekers.userservice.services.kafka.consumers

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.careerseekers.userservice.cache.UsersStatisticsClient
import org.careerseekers.userservice.dto.UserLoginMessageDto
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Service

@Service
class UserLoginKafkaConsumer(private val usersStatisticsClient: UsersStatisticsClient) :
    CustomKafkaConsumer<String, UserLoginMessageDto> {

    @KafkaListener(
        topics = ["STATISTICS_UPDATE"],
        groupId = "STATISTICS_UPDATER"
    )
    override fun receiveMessage(
        consumerRecord: ConsumerRecord<String, UserLoginMessageDto>,
        acknowledgment: Acknowledgment
    ) {
        val message = consumerRecord.value()
        usersStatisticsClient.trackUserActivity(message.email)

        acknowledgment.acknowledge()
    }


}