package org.careerseekers.userservice.dto.events

import org.springframework.context.ApplicationEvent

class DauUpdatedEvent(source: Any, val count: Long) : ApplicationEvent(source)