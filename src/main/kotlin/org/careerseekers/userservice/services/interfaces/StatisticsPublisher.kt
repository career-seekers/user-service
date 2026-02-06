package org.careerseekers.userservice.services.interfaces

import org.careerseekers.userservice.dto.statistics.StatisticsSnapshot

interface StatisticsPublisher {
    fun publishDauUpdate(count: Long)
    fun publishWauUpdate(count: Long)
    fun publishMauUpdate(count: Long)

    fun publishAllStats(item: StatisticsSnapshot)
}