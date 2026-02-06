package org.careerseekers.userservice.services.kafka.consumers

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.careerseekers.userservice.cache.UsersStatisticsClient
import org.careerseekers.userservice.dto.UserLoginMessageDto
import org.careerseekers.userservice.services.StatisticsEventPublisher
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Service

@Service
class UserLoginKafkaConsumer(
    private val usersStatisticsClient: UsersStatisticsClient,
    private val statisticsEventPublisher: StatisticsEventPublisher,
) :
    CustomKafkaConsumer<String, UserLoginMessageDto> {

    @KafkaListener(
        topics = ["USER_LOGIN_TOPIC"],
        groupId = "USER_LOGIN_TOPIC"
    )
    override fun receiveMessage(
        consumerRecord: ConsumerRecord<String, UserLoginMessageDto>,
        acknowledgment: Acknowledgment
    ) {
        val message = consumerRecord.value()
        val snapshot = usersStatisticsClient.trackUserActivity(message.email)

        statisticsEventPublisher.publishAllStats(snapshot)

        acknowledgment.acknowledge()
    }


}