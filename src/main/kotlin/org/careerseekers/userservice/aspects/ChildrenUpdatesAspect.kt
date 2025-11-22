package org.careerseekers.userservice.aspects

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.careerseekers.userservice.aspects.interfaces.IEntityUpdatesAspect
import org.careerseekers.userservice.controllers.WebSocketStatisticController
import org.careerseekers.userservice.repositories.ChildrenRepository
import org.careerseekers.userservice.services.StatisticScrapperService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Aspect
@Component
class ChildrenUpdatesAspect(
    private val scrapperService: StatisticScrapperService,
    private val childrenRepository: ChildrenRepository,
    private val webSocketStatisticController: WebSocketStatisticController,
) : IEntityUpdatesAspect {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @AfterReturning("@annotation(org.careerseekers.userservice.annotations.ChildrenUpdate)")
    override fun afterUpdate(joinPoint: JoinPoint) {
        val children = childrenRepository.findAll()
        scrapperService.setChildrenCount(children)

        webSocketStatisticController.sendStatisticsManually()
        logger.info("Children count updated, current count: ${children.size}")
    }
}