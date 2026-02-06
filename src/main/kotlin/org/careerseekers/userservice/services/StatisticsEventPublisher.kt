package org.careerseekers.userservice.services

import org.careerseekers.userservice.dto.events.DauUpdatedEvent
import org.careerseekers.userservice.dto.events.MauUpdatedEvent
import org.careerseekers.userservice.dto.events.StatsUpdatedEvent
import org.careerseekers.userservice.dto.events.WauUpdatedEvent
import org.careerseekers.userservice.dto.statistics.StatisticsSnapshot
import org.careerseekers.userservice.services.interfaces.StatisticsPublisher
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service

@Service
class StatisticsEventPublisher(private val applicationEventPublisher: ApplicationEventPublisher) : StatisticsPublisher {

    override fun publishDauUpdate(count: Long) =
        applicationEventPublisher.publishEvent(DauUpdatedEvent(this, count))

    override fun publishWauUpdate(count: Long) {
        applicationEventPublisher.publishEvent(WauUpdatedEvent(this, count))
    }

    override fun publishMauUpdate(count: Long) {
        applicationEventPublisher.publishEvent(MauUpdatedEvent(this, count))
    }

    override fun publishAllStats(item: StatisticsSnapshot) {
        applicationEventPublisher.publishEvent(StatsUpdatedEvent(this, item.dau, item.wau, item.mau))
    }
}