package org.careerseekers.userservice.dto.events

import org.springframework.context.ApplicationEvent

class StatsUpdatedEvent(
    source: Any,
    val dau: Long,
    val wau: Long,
    val mau: Long
) : ApplicationEvent(source)
