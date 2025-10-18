package org.careerseekers.userservice.aspects

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.careerseekers.userservice.aspects.interfaces.IEntityUpdatesAspect
import org.careerseekers.userservice.repositories.UsersRepository
import org.careerseekers.userservice.utils.StatisticScrapperService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Aspect
@Component
class UsersUpdatesAspect(
    private val scrapperService: StatisticScrapperService,
    private val usersRepository: UsersRepository,
) : IEntityUpdatesAspect {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @AfterReturning("@annotation(org.careerseekers.userservice.annotations.UsersUpdate)")
    override fun afterUpdate(joinPoint: JoinPoint) {
        val users = usersRepository.findAll()

        scrapperService.setTutorsInfo(users)
        scrapperService.setExpertsInfo(users)
        scrapperService.setMentorInfo(users)
        scrapperService.setUsersInfo(users)

        logger.info("Users statistics updated")
    }
}