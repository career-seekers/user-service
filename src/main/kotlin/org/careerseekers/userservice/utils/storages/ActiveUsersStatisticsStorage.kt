package org.careerseekers.userservice.utils.storages

import org.careerseekers.userservice.dto.events.StatsUpdatedEvent
import org.careerseekers.userservice.dto.statistics.StatisticsSnapshot
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import java.util.concurrent.atomic.AtomicLong

@Component
class ActiveUsersStatisticsStorage {

    private val dau: AtomicLong = AtomicLong(0)
    private val wau: AtomicLong = AtomicLong(0)
    private val mau: AtomicLong = AtomicLong(0)

    fun getStatistics() = StatisticsSnapshot(
        dau = dau.get(),
        wau = wau.get(),
        mau = mau.get()
    )

    @EventListener
    fun updateStats(event: StatsUpdatedEvent) {
        dau.set(event.dau)
        wau.set(event.wau)
        mau.set(event.mau)
    }
}