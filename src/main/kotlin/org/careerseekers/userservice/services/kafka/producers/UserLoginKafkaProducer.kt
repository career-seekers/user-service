package org.careerseekers.userservice.services.kafka.producers

import org.careerseekers.userservice.dto.UserLoginMessageDto
import org.careerseekers.userservice.enums.KafkaTopics
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class UserLoginKafkaProducer(
    override val template: KafkaTemplate<String, UserLoginMessageDto>
) : CustomKafkaProducer<UserLoginMessageDto> {
    override val topic = KafkaTopics.USER_LOGIN_TOPIC
}