package org.careerseekers.userservice.controllers

import org.careerseekers.userservice.dto.events.StatsUpdatedEvent
import org.careerseekers.userservice.dto.statistics.StatisticsSnapshot
import org.careerseekers.userservice.dto.statistics.UsersStatisticPairDto
import org.careerseekers.userservice.utils.storages.ActiveUsersStatisticsStorage
import org.careerseekers.userservice.utils.storages.StatisticsStorage
import org.springframework.context.event.EventListener
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller

@Controller
class WebSocketStatisticController(
    private val messagingTemplate: SimpMessagingTemplate,
    private val activeUsersStatisticsStorage: ActiveUsersStatisticsStorage,
) {

    data class StatisticsMessage(
        val tutorsInfo: UsersStatisticPairDto,
        val expertsInfo: UsersStatisticPairDto,
        val mentorsInfo: UsersStatisticPairDto,
        val usersInfo: UsersStatisticPairDto,
        val childrenCount: Int,
    )

    @MessageMapping("/getStatistics")
    @SendTo("/users-service/topic/statistics")
    fun sendStatistics(): StatisticsMessage {
        return StatisticsMessage(
            tutorsInfo = StatisticsStorage.tutorsInfo,
            expertsInfo = StatisticsStorage.expertsInfo,
            mentorsInfo = StatisticsStorage.mentorsInfo,
            usersInfo = StatisticsStorage.usersInfo,
            childrenCount = StatisticsStorage.childrenCount,
        )
    }

    fun sendStatisticsManually() {
        val statistics = sendStatistics()
        messagingTemplate.convertAndSend("/users-service/topic/statistics", statistics)
    }

    @MessageMapping("/getActiveUsersStatistics")
    @SendTo("/users-service/topic/activeUsersStatistics")
    fun sendActiveUsersStatistics(): StatisticsSnapshot = activeUsersStatisticsStorage.getStatistics()

    @EventListener
    fun sendActiveUsersStatisticsManually(event: StatsUpdatedEvent) {
        val statistics = StatisticsSnapshot(
            dau = event.dau,
            wau = event.wau,
            mau = event.mau,
        )
        messagingTemplate.convertAndSend("/users-service/topic/activeUsersStatistics", statistics)
    }
}