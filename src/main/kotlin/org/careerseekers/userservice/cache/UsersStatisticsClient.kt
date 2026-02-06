package org.careerseekers.userservice.cache

import org.careerseekers.userservice.dto.statistics.StatisticsSnapshot
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.temporal.WeekFields

@Component
class UsersStatisticsClient(
    @param:Qualifier("usersStatistics")
    val redisTemplate: RedisTemplate<String, String>,
) {
    private val setOps = redisTemplate.opsForSet()
    private val prefix = "usersStatistics"

    fun trackUserActivity(userId: String): StatisticsSnapshot {
        val today = "$prefix:dau:${LocalDate.now()}"
        val week = "$prefix:wau:${weekKey()}"
        val month = "$prefix:mau:${monthKey()}"

        setOps.add(today, userId)
        setOps.add(week, userId)
        setOps.add(month, userId)

        return StatisticsSnapshot(
            dau = setOps.size(today) ?: 0,
            wau = setOps.size(week) ?: 0,
            mau = setOps.size(month) ?: 0
        )
    }

    private fun weekKey(): String {
        val now = LocalDate.now()
        val weekNumber = now[WeekFields.ISO.weekOfWeekBasedYear()]
        return "${now.year}-W${weekNumber.toString().padStart(2, '0')}"
    }

    private fun monthKey(): String {
        val now = LocalDate.now()
        return "${now.year}-${now.monthValue.toString().padStart(2, '0')}"
    }
}