package org.careerseekers.userservice.services.kafka.producers

import org.careerseekers.userservice.dto.TgLinkNotificationDto
import org.careerseekers.userservice.enums.KafkaTopics
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class KafkaTgLinksNotificationProducer(
    override val template: KafkaTemplate<String, TgLinkNotificationDto>,
) : CustomKafkaProducer<TgLinkNotificationDto> {
    override val topic = KafkaTopics.TG_LINKS_NOTIFICATION
}